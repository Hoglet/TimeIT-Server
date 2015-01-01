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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
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
	private static UserDAO				userdao;
	static TaskDAO						taskdao;
	private static DateTime				now		= DateTime.now();

	@BeforeClass
	public static void beforeClass()
	{
		user = new User("testman", "Test Tester", "password", "", null);
		userdao = new UserDAO(emf);
		userdao.add(user);
		task = new Task(UUID.randomUUID(), "Task1", null, false, now, false, user);
		taskdao = new TaskDAO(emf);
		taskdao.add(task);
	}

	@AfterClass
	public static void afterClass()
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		TypedQuery<Task> getQuery = em.createQuery("SELECT t FROM Task t", Task.class);
		List<Task> tasks = getQuery.getResultList();
		for (Task task : tasks)
		{
			em.remove(task);
		}
		em.remove(em.getReference(User.class, user.getUsername()));
		em.getTransaction().commit();
		em.close();
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
		TypedQuery<Time> getTimeQuery = em.createQuery("SELECT t FROM Time t", Time.class);
		List<Time> times = getTimeQuery.getResultList();
		for (Time time : times)
		{
			em.remove(time);
		}
		em.getTransaction().commit();
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
		Assert.assertEquals(times.size(), 0);
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
}
