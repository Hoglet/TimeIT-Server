package resources;

import io.dropwizard.testing.junit.ResourceTestRule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TimesSyncResource;

import com.sun.jersey.api.client.GenericType;

public class TestTimesSyncResource
{

	private static final String				TESTMAN_ID	= "testman";
	private static EntityManagerFactory		emf			= Persistence.createEntityManagerFactory("test");
	private static UserDAO					userdao		= new UserDAO(emf);
	private static TaskDAO					taskdao		= new TaskDAO(emf);
	private static TimeDAO					timedao		= new TimeDAO(emf);
	private static User						user;
	private static Task						task;

	private static GenericType<List<Time>>	returnType	= new GenericType<List<Time>>()
														{
														};
	private static Time						time;

	@ClassRule
	public static final ResourceTestRule	resources	= ResourceTestRule.builder()
																.addResource(new TimesSyncResource(emf)).build();

	@BeforeClass
	public static void beforeClass()
	{
		user = new User(TESTMAN_ID, TESTMAN_ID, "password", "", new ArrayList<Role>());
		userdao.add(user);
		task = new Task("123", "Task1", "", false, 0, false, user);
		taskdao.add(task);
		time = new Time("1", 10, 100, false, 100, task);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Before
	public void setUp()
	{
	}

	@After
	public void tearDown()
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		TypedQuery<Time> getQuery = em.createQuery("SELECT t FROM Time t", Time.class);
		List<Time> times = getQuery.getResultList();
		for (Time time : times)
		{
			em.remove(time);
		}
		em.getTransaction().commit();
	}

	@Test
	public void testTimesGet() throws SQLException
	{
		timedao.add(time);
		List<Time> resultingTimes = resources.client().resource("/sync/times/testman").accept("application/json")
				.get(returnType);
		Assert.assertEquals(resultingTimes.size(), 1);
		Time resultingTime = resultingTimes.get(0);
		Assert.assertTrue(resultingTime.equals(time));
	}

	@Test
	public void testTasksSync()
	{
		List<Time> timesToSend = new ArrayList<Time>();
		Time newTime = new Time("2", 11, 101, false, 101, task);
		timesToSend.add(newTime);
		List<Time> resultingTimes = resources.client().resource("/sync/times/testman").accept("application/json")
				.type("application/json").put(returnType, timesToSend);
		Assert.assertEquals("Number of times returned", 1, resultingTimes.size());
		Time resultingTime = resultingTimes.get(0);
		Assert.assertTrue(resultingTime.equals(newTime));
	}

}
