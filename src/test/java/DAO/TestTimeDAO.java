package DAO;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

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
	private static final UUID           timeID  = UUID.randomUUID();
	public static EntityManagerFactory  emf     = Persistence.createEntityManagerFactory("test");
	private final TimeDAO               timedao = new TimeDAO(emf);

	private static User                 user;
	private static Task                 task;
	private static Task                 task2;
	private static UserDAO              userdao;
	static TaskDAO                      taskdao;
	private static Instant              now     = Instant.now();
	private        Instant              epoch   = Instant.ofEpochSecond(0);
	private        Instant              stop1   = Instant.ofEpochSecond(1000);
	private static ZoneId               zone;
	private static Task                 child;
	private static String               comment = "Just a comment";

	@BeforeClass
	public static void beforeClass()
	{
		user = new User("testman", "Test Tester", "password", "", null);
		userdao = new UserDAO(emf);
		userdao.add(user);
		task = new Task(UUID.randomUUID(), "Task1", null, false, false, user);
		child = new Task(UUID.randomUUID(), "Task1", task, false, false, user);
		task2 = new Task(UUID.randomUUID(), "Task2", null, false, false, user);
		taskdao = new TaskDAO(emf);
		taskdao.add(task);
		taskdao.add(task2);
		taskdao.add(child);
		zone = ZonedDateTime.now().getZone();
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
		em.getTransaction().begin();
		Query query = em.createQuery("DELETE FROM Time t");
		query.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}

	@Test
	public final void testUpdate() throws SQLException
	{
		Time time = new Time(timeID, epoch, now, false, epoch, task, "");
		timedao.add(time);
		Time t2 = new Time(timeID, now, now, false, now, task, comment);
		timedao.update(t2);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Time result = (Time) times.toArray()[0];
		Assert.assertEquals(t2, result);
	}

	@Test
	public final void testAdd_Existing() throws SQLException
	{
		Time time = new Time(timeID, epoch, stop1, false, now, task, comment);
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

		Time time = new Time(timeID, epoch, stop1, false, now, task, comment);
		timedao.add(time);

		Instant start2 = Instant.ofEpochSecond(1000);
		Instant stop2 = Instant.ofEpochSecond(10000);

		Time time2 = new Time(UUID.randomUUID(), start2, stop2, true, now, task, comment);
		timedao.add(time2);
		times = timedao.getTimes(user.getUsername());
		Assert.assertEquals(2, times.size());
	}

	@Test
	public final void testGetTimesRanged() throws SQLException
	{
		Time time = new Time(timeID, epoch, stop1, false, now, task, comment);
		timedao.add(time);
		
		Instant start2 = Instant.ofEpochSecond(1000);
		Instant stop2 = Instant.ofEpochSecond(10000);
		Time time2 = new Time(UUID.randomUUID(), start2, stop2, true, now, task, comment);
		timedao.add(time2);
		Collection<Time> times = timedao.getTimes(user.getUsername(), now.minusSeconds(1));
		Assert.assertEquals(2, times.size());
		times = timedao.getTimes(user.getUsername(), now.plusSeconds(1));
		Assert.assertEquals(0, times.size());

	}

	@Test
	public final void testUpdateOrAdd_addOnEmpty() throws SQLException
	{
		Time time = new Time(timeID, epoch, stop1, false, now, task, comment);
		Time[] timeArray = new Time[] { time };
		timedao.updateOrAdd(timeArray);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Assert.assertEquals(times.size(), 1);
	}

	@Test
	public final void testUpdateOrAdd_update() throws SQLException
	{
		Time time = new Time(timeID, epoch, now, false, epoch, task, comment);
		timedao.add(time);
		Time t2 = new Time(timeID, now, now, false, now, task, comment);
		Time[] timeArray = new Time[] { t2 };
		timedao.updateOrAdd(timeArray);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Time result = (Time) times.toArray()[0];
		Assert.assertEquals(t2.getStart(), result.getStart());
	}

	@Test
	public final void testUpdateOrAdd_noUpdatWhenOlder() throws SQLException
	{
		Time time = new Time(timeID, epoch , stop1, false, now, task, comment);
		timedao.add(time);
		
		Instant start2 = Instant.ofEpochSecond(700);
		Instant stop2 = Instant.ofEpochSecond(1000);
		
		Time t3 = new Time(timeID, start2, stop2, false, epoch, task, comment);
		Time[] timeArray = new Time[] { t3 };
		timedao.updateOrAdd(timeArray);
		Collection<Time> times = timedao.getTimes(user.getUsername());
		Time result = (Time) times.toArray()[0];
		Assert.assertEquals("R3", result.getStart(), time.getStart());
	}

	@Test
	public final void testUpdateOrAdd_dummy() throws SQLException
	{
		Instant start2 = Instant.ofEpochSecond(700);
		Instant stop2 = Instant.ofEpochSecond(1000);
		Time t3 = new Time(timeID, start2, stop2, false, now, task, comment);
		Time[] timeArray = new Time[] { t3 };
		timedao.updateOrAdd(timeArray);
		timedao.updateOrAdd(timeArray);
	}

	@Test
	public final void testGetTimesSummary_simple() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.withHour(10).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(60), false, l_now.toInstant(), task, comment);
		timedao.add(time);
		Time deletedTime = new Time(UUID.randomUUID(), start, start.plusSeconds(1000), true, l_now.toInstant(), task, comment);
		timedao.add(deletedTime);

		ZonedDateTime startOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, startOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);

		Assert.assertEquals(task, item.getTask());
		Duration expectedDuration = Duration.ofSeconds(60);
		Assert.assertEquals(expectedDuration, item.getDuration());
	}

	@Test
	public final void testGetTimesSummary_timeStartsBeforeMidnight() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.minusDays(1).withHour(23).withMinute(0).withSecond(0).withNano(0).toInstant();
		Instant stop = l_now.withHour(0).withMinute(10).withSecond(0).withNano(0).toInstant();
		
		Time time = new Time(timeID, start, stop, false, now, task, comment);
		timedao.add(time);
		
		Time deletedTime = new Time(UUID.randomUUID(), start, stop, true, now, task2, comment);
		timedao.add(deletedTime);
		
		ZonedDateTime startOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		
		TimeDescriptorList result = timedao.getTimes(user, startOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);

		Assert.assertEquals(task, item.getTask());

		Duration expectedDuration = Duration.between(startOfDay.toInstant(), stop);
		Assert.assertEquals(expectedDuration, item.getDuration());

	}

	@Test
	public final void testGetTimesSummary_timeEndsAfteMidnight() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.withHour(23).withMinute(50).withSecond(0).withNano(0).toInstant();
		Instant stop = l_now.plusDays(1).withHour(0).withMinute(10).withSecond(0).withNano(0).toInstant();
		Time time = new Time(timeID, start, stop, false, now, task, comment);
		timedao.add(time);
		Time deletedTime = new Time(UUID.randomUUID(), start, stop, true, now, task, comment);
		timedao.add(deletedTime);
		ZonedDateTime startOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX).withNano(0);
		TimeDescriptorList result = timedao.getTimes(user, startOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);

		Assert.assertEquals(task, item.getTask());
		
		Instant startI = Instant.from(start);
		Instant eodI  = Instant.from(endOfDay);
		Duration expectedDuration = Duration.between(startI, eodI);
		Assert.assertEquals(expectedDuration, item.getDuration());
	}

	@Test
	public final void testGetTimesSummary_timeBeforeLimits() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.minusDays(1).withHour(10).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(60), false, now, task, comment);
		timedao.add(time);
		ZonedDateTime beginningOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public final void testGetTimesSummary_timeAfterLimits() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.plusDays(1).withHour(10).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(60000), false, now, task, comment);
		timedao.add(time);
		ZonedDateTime beginningOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(0, result.size());
	}

	@Test
	public final void testGetTimesSummary_twoTimes() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.withHour(10).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(60000), false, now, task, comment);
		timedao.add(time);
		Instant start2 = l_now.withHour(12).toInstant();
		UUID timeID2 = UUID.randomUUID();
		Time time2 = new Time(timeID2, start2, start2.plusSeconds(60000), false, now, task, comment);
		timedao.add(time2);
		ZonedDateTime beginningOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public final void testGetTimesSummary_twoTimes_onePassingMargin() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.plusDays(1).with(LocalTime.MIN).minusSeconds(2).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, task, comment);
		timedao.add(time);
		Instant start2 = l_now.withHour(12).toInstant();
		UUID timeID2 = UUID.randomUUID();
		Time time2 = new Time(timeID2, start2, start2.plusSeconds(60), false, now, task, comment);
		timedao.add(time2);

		ZonedDateTime beginningOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(1, result.size());
		TimeDescriptor item = result.get(0);
		Duration expected = Duration.ofSeconds(60 + 1);
		Assert.assertEquals(expected, item.getDuration());
	}

	@Test
	public final void testGetTimesSummary_dummy() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.withHour(10).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, task, comment);
		Time time2 = new Time(UUID.randomUUID(), start, start.plusSeconds(5), false, now, task2, comment);
		Time time3 = new Time(UUID.randomUUID(), start, start.plusSeconds(5), false, now, task2, comment);
		timedao.add(time2);
		timedao.add(time);
		timedao.add(time3);

		ZonedDateTime beginningOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public final void testTimeHierarchy() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.withHour(10).toInstant();
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, child, comment);
		timedao.add(time);

		ZonedDateTime beginningOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		TimeDescriptorList result = timedao.getTimes(user, beginningOfDay, endOfDay);
		Assert.assertEquals(2, result.size());

		TimeDescriptor parent = result.get(0);
		Duration expected = Duration.ofSeconds(0);
		Assert.assertEquals(expected, parent.getDuration());
		expected = Duration.ofSeconds(5);
		Assert.assertEquals(expected, parent.getDurationWithChildren());
	}

	@Test
	public final void testGetTimeItems() throws SQLException
	{
		ZonedDateTime l_now = now.atZone(zone);
		Instant start = l_now.withHour(10).toInstant();
		ZonedDateTime beginingOfDay = l_now.with(LocalTime.MIN);
		ZonedDateTime endOfDay = l_now.with(LocalTime.MAX);
		Time time = new Time(timeID, start, start.plusSeconds(5), false, now, task2, comment);
		Time time2 = new Time(UUID.randomUUID(), beginingOfDay.minusHours(1).toInstant(), beginingOfDay.plusHours(1).toInstant(), false, now, task2, comment);
		Time time3 = new Time(UUID.randomUUID(), endOfDay.minusHours(1).toInstant(), endOfDay.plusHours(2).toInstant(), false, now, task2, comment);
		Time time4 = new Time(UUID.randomUUID(), endOfDay.plusHours(1).toInstant(), endOfDay.plusHours(2).toInstant(), false, now, task2, comment);
		Time time5 = new Time(UUID.randomUUID(), beginingOfDay.minusHours(2).toInstant(), beginingOfDay.minusHours(1).toInstant(), false, now, task2, comment);
		timedao.add(time);
		timedao.add(time2);
		timedao.add(time3);
		timedao.add(time4);
		timedao.add(time5);
		List<Time> actual = timedao.getTimeItems(task2, beginingOfDay, endOfDay);
		Assert.assertEquals(3, actual.size());
	}
}
