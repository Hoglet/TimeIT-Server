package DAO;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

public class TestTimeDAO
{
	private static final UUID			timeID	= UUID.randomUUID();
	public static EntityManagerFactory	emf		= Persistence.createEntityManagerFactory("test");
	private final TimeDAO				timedao	= new TimeDAO(emf);

	private static User					user;
	private static Task					task;
	private static Task					task2;
	private static UserDAO				userdao;
	static TaskDAO						taskdao;
	private static DateTime				now		= DateTime.now();
	private static Task					child;

	@BeforeClass
	public static void beforeClass()
	{
		user = new User("testman", "Test Tester", "password", "", null);
		userdao = new UserDAO(emf);
		userdao.add(user);
		task = new Task(UUID.randomUUID(), "Task1", null, false, now, false, user);
		child = new Task(UUID.randomUUID(), "Task1", task, false, now, false, user);
		task2 = new Task(UUID.randomUUID(), "Task2", null, false, now, false, user);
		taskdao = new TaskDAO(emf);
		taskdao.add(task);
		taskdao.add(task2);
		taskdao.add(child);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception
	{
		EntityManager em = emf.createEntityManager();
		TypedQuery<Time> getTimeQuery = em.createQuery("SELECT t FROM Time t", Time.class);
		List<Time> times;
		do
		{
			em.getTransaction().begin();
			times = getTimeQuery.getResultList();
			for (Time time : times)
			{
				em.remove(time);
			}
			em.getTransaction().commit();
		} while (times.size() > 0);
		em.close();
	}

	@Test
	public final void testUpdate() throws SQLException
	{
		Time time = new Time(timeID, new DateTime(0), now, false, new DateTime(0), task);
		timedao.add(time);
		Time t2 = new Time(timeID, now, now, false, now, task);
		timedao.update(t2);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Time result = (Time) times.toArray()[0];
		Assert.assertEquals(t2, result);
	}

	@Test
	public final void testAdd_Existing() throws SQLException
	{
		Time time = new Time(timeID, new DateTime(0), new DateTime(1000 * 1000), false, now, task);
		timedao.add(time);
		try
		{
			timedao.add(time);
			Assert.fail("Should throw exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals(RollbackException.class, e.getClass());
		}
	}

	@Test
	public final void testGetTimes() throws SQLException
	{
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Assert.assertEquals(0, times.size());
		Time time = new Time(timeID, new DateTime(0), new DateTime(1000 * 1000), false, now, task);
		timedao.add(time);
	}

	@Test
	public final void testUpdateOrAdd_addOnEmpty() throws SQLException
	{
		Time time = new Time(timeID, new DateTime(0), new DateTime(1000), false, now, task);
		Time[] timeArray = new Time[] { time };
		timedao.updateOrAdd(timeArray);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Assert.assertEquals(times.size(), 1);
	}

	@Test
	public final void testUpdateOrAdd_update() throws SQLException
	{
		Time time = new Time(timeID, new DateTime(0), now, false, new DateTime(0), task);
		timedao.add(time);
		Time t2 = new Time(timeID, now, now, false, now, task);
		Time[] timeArray = new Time[] { t2 };
		timedao.updateOrAdd(timeArray);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Time result = (Time) times.toArray()[0];
		Assert.assertEquals(t2.getStart().getMillis(), result.getStart().getMillis());
	}

	@Test
	public final void testUpdateOrAdd_noUpdatWhenOlder() throws SQLException
	{
		Time time = new Time(timeID, new DateTime(0), new DateTime(1000 * 1000), false, now, task);
		timedao.add(time);
		Time t3 = new Time(timeID, new DateTime(700 * 1000), new DateTime(1000 * 1000), false, new DateTime(0), task);
		Time[] timeArray = new Time[] { t3 };
		timedao.updateOrAdd(timeArray);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Time result = (Time) times.toArray()[0];
		Assert.assertEquals("R3", result.getStart(), time.getStart());
	}

	@Test
	public final void testUpdateOrAdd_dummy() throws SQLException
	{
		Time t3 = new Time(timeID, new DateTime(700 * 1000), new DateTime(1000 * 1000), false, now, task);
		Time[] timeArray = new Time[] { t3 };
		timedao.updateOrAdd(timeArray);
		timedao.updateOrAdd(timeArray);
	}

	@Test
	public final void testGetTimesSummary_simple() throws SQLException
	{
		DateTime start = now.withHourOfDay(10);
		Time time = new Time(timeID, start, start.plus(60000), false, now, task);
		timedao.add(time);
		DateTime startOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, startOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);

		Assert.assertEquals(task, item.getTask());
		Duration expectedDuration = new Duration(60000);
		Assert.assertEquals(expectedDuration, item.getDuration());
	}

	@Test
	public final void testGetTimesSummary_timeStartsBeforeMidnight() throws SQLException
	{
		DateTime start = now.minusDays(1).withTime(23, 0, 0, 0);
		DateTime stop = now.withTime(0, 10, 0, 0);
		Time time = new Time(timeID, start, stop, false, now, task);
		timedao.add(time);
		DateTime startOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, startOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);

		Assert.assertEquals(task, item.getTask());
		Duration expectedDuration = new Duration(stop.getMillis() - startOfDay.getMillis());
		Assert.assertEquals(expectedDuration, item.getDuration());

	}

	@Test
	public final void testGetTimesSummary_timeEndsAfteMidnight() throws SQLException
	{
		DateTime start = now.withTime(23, 50, 0, 0);
		DateTime stop = now.plusDays(1).withTime(0, 10, 0, 0);
		Time time = new Time(timeID, start, stop, false, now, task);
		timedao.add(time);
		DateTime startOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, startOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);

		Assert.assertEquals(task, item.getTask());
		Duration expectedDuration = new Duration(endOfDay.getMillis() - start.getMillis());
		Assert.assertEquals(expectedDuration, item.getDuration());
	}

	@Test
	public final void testGetTimesSummary_timeBeforeLimits() throws SQLException
	{
		DateTime start = now.minusDays(1).withHourOfDay(10);
		Time time = new Time(timeID, start, start.plus(60000), false, now, task);
		timedao.add(time);
		DateTime beginningOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public final void testGetTimesSummary_timeAfterLimits() throws SQLException
	{
		DateTime start = now.plusDays(1).withHourOfDay(10);
		Time time = new Time(timeID, start, start.plus(60000), false, now, task);
		timedao.add(time);
		DateTime beginningOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public final void testGetTimesSummary_twoTimes() throws SQLException
	{
		DateTime start = now.withHourOfDay(10);
		Time time = new Time(timeID, start, start.plus(60000), false, now, task);
		timedao.add(time);
		DateTime start2 = now.withHourOfDay(12);
		UUID timeID2 = UUID.randomUUID();
		Time time2 = new Time(timeID2, start2, start2.plus(60000), false, now, task);
		timedao.add(time2);
		DateTime beginningOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public final void testGetTimesSummary_twoTimes_onePassingMargin() throws SQLException
	{
		DateTime start = now.plusDays(1).withTimeAtStartOfDay().minusSeconds(2);
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, task);
		timedao.add(time);
		DateTime start2 = now.withHourOfDay(12);
		UUID timeID2 = UUID.randomUUID();
		Time time2 = new Time(timeID2, start2, start2.plus(60000), false, now, task);
		timedao.add(time2);

		DateTime beginningOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);
		Duration expected = new Duration(60000 + 1000);
		Assert.assertEquals(expected, item.getDuration());
	}

	@Test
	public final void testGetTimesSummary_dummy() throws SQLException
	{
		DateTime start = now.withHourOfDay(10);
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, task);
		Time time2 = new Time(UUID.randomUUID(), start, start.plusSeconds(5), false, now, task2);
		Time time3 = new Time(UUID.randomUUID(), start, start.plusSeconds(5), false, now, task2);
		timedao.add(time2);
		timedao.add(time);
		timedao.add(time3);

		DateTime beginningOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public final void testTimeHierarchy() throws SQLException
	{
		DateTime start = now.withHourOfDay(10);
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, child);
		timedao.add(time);

		DateTime beginningOfDay = now.withTimeAtStartOfDay();
		DateTime endOfDay = now.withTime(23, 59, 59, 0);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(2, result.size());

		TimeDescriptor parent = result.get(0);
		Duration expected = new Duration(0);
		Assert.assertEquals(expected, parent.getDuration());
		expected = new Duration(5000);
		Assert.assertEquals(expected, parent.getDurationWithChildren());
	}

}
