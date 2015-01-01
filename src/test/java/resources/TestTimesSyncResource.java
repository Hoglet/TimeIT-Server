package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.HttpHeaders;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TimesSyncResource;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class TestTimesSyncResource
{

	private static final String				TESTMAN_ID		= "testman";
	private static EntityManagerFactory		emf				= Persistence.createEntityManagerFactory("test");
	private static UserDAO					userdao			= new UserDAO(emf);
	private static TaskDAO					taskdao			= new TaskDAO(emf);
	private static TimeDAO					timedao			= new TimeDAO(emf);
	private static User						user;
	private static Task						task;
	private static GenericType<List<Time>>	returnType		= new GenericType<List<Time>>()
															{
															};
	private static Time						time;

	private static BasicAuthProvider<User>	myAuthenticator	= new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");
	private static DateTime					now;

	@ClassRule
	public static final ResourceTestRule	resources		= ResourceTestRule
																	.builder()
																	.addProvider(
																			new ContextInjectableProvider<HttpHeaders>(
																					HttpHeaders.class, null))
																	.addResource(myAuthenticator)

																	.addResource(new TimesSyncResource(emf)).build();

	private WebResource						resource;

	@BeforeClass
	public static void beforeClass()
	{
		now = DateTime.now();
		user = new User(TESTMAN_ID, TESTMAN_ID, "password", "", new ArrayList<Role>());
		userdao.add(user);
		task = new Task("123", "Task1", null, false, now, false, user);
		taskdao.add(task);
		time = new Time("1", new DateTime(10 * 1000), new DateTime(100 * 1000), false, now, task);
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
		resource = resources.client().resource("/sync/times/testman");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		List<Time> resultingTimes = resource.get(returnType);
		Assert.assertEquals(resultingTimes.size(), 1);
		Time resultingTime = resultingTimes.get(0);
		Assert.assertTrue(resultingTime.equals(time));
	}

	@Test
	public void testTimesGet_attackOtherUser() throws SQLException
	{
		timedao.add(time);
		resource = resources.client().resource("/sync/times/otherman");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.get(returnType);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testTimesSync()
	{
		List<Time> timesToSend = new ArrayList<Time>();
		Time newTime = new Time("2", new DateTime(11 * 1000), new DateTime(101 * 1000), false, now, task);
		timesToSend.add(newTime);
		resource = resources.client().resource("/sync/times/testman");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		List<Time> resultingTimes = resource.type("application/json").put(returnType, timesToSend);
		Assert.assertEquals("Number of times returned", 1, resultingTimes.size());
		Time resultingTime = resultingTimes.get(0);
		Assert.assertTrue(resultingTime.equals(newTime));
	}

	@Test
	public void testTimesSync_attackOtherUser()
	{
		List<Time> timesToSend = new ArrayList<Time>();
		Time newTime = new Time("2", new DateTime(11 * 1000), new DateTime(101 * 1000), false, now, task);
		timesToSend.add(newTime);
		resource = resources.client().resource("/sync/times/otherman");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.type("application/json").put(returnType, timesToSend);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testTimesSync_attackDirtyData()
	{
		List<Time> timesToSend = new ArrayList<Time>();
		User otherUser = new User("innocent", "bystander", "unkown", "", null);
		userdao.add(otherUser);
		Task otherTask = new Task("42", "d", null, false, now, false, otherUser);
		taskdao.add(otherTask);
		Time newTime = new Time("2", new DateTime(11 * 1000), new DateTime(101 * 1000), false, now, otherTask);
		timesToSend.add(newTime);
		resource = resources.client().resource("/sync/times/testman");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.type("application/json").put(returnType, timesToSend);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

}
