package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.HttpHeaders;

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

	private static final String             TESTMAN_ID = "testman";
	private static EntityManagerFactory     emf        = Persistence.createEntityManagerFactory("test");
	private static UserDAO                  userdao    = new UserDAO(emf);
	private static TaskDAO                  taskdao    = new TaskDAO(emf);
	private static TimeDAO                  timedao    = new TimeDAO(emf);
	private static User                     user;
	private static Task                     task;
	private static GenericType<List<Time>>  returnType = new GenericType<List<Time>>()
															{
															};
	private static Time                     time;

	private static BasicAuthProvider<User>  myAuthenticator = new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");
	private static Instant                  now;
	private        Instant                  start   = Instant.ofEpochSecond(11);
	private        Instant                  stop    = Instant.ofEpochSecond(101);

	private static UUID						timeID  = UUID.randomUUID();
	private final static String             comment = "Just a comment";

	@ClassRule
	public static final ResourceTestRule	resources = ResourceTestRule.builder()
	                                                                    .addProvider(
	                                                                            new ContextInjectableProvider<HttpHeaders>(
	                                                                                    HttpHeaders.class, null))
	                                                                    .addResource(myAuthenticator)
	                                                                    .addResource(new TimesSyncResource(emf)).build();

	private WebResource                     resource;

	@BeforeClass
	public static void beforeClass()
	{
		now = Instant.ofEpochSecond(100);
		user = new User(TESTMAN_ID, TESTMAN_ID, "password", "", new ArrayList<Role>());
		userdao.add(user);
		task = new Task(UUID.randomUUID(), "Task1", null, false, now, false, user);
		taskdao.add(task);

		Instant  l_start = Instant.ofEpochSecond(10);
		Instant  l_stop  = Instant.ofEpochSecond(100);

		time = new Time(timeID, l_start, l_stop, false, now, task, comment);
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

		Time newTime = new Time(timeID, start, stop, false, now, task, comment);
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
	public void testTimesSyncRanged()
	{
		List<Time> timesToSend = new ArrayList<Time>();
		Instant  changed = Instant.ofEpochSecond(100);

		Time newTime = new Time(timeID, start, stop, false, changed, task, comment);
		timesToSend.add(newTime);
		resource = resources.client().resource("/sync/times/testman/101");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		List<Time> resultingTimes = resource.type("application/json").put(returnType, timesToSend);
		Assert.assertEquals("Number of times returned", 0, resultingTimes.size());

		resource = resources.client().resource("/sync/times/testman/100");
		resource.accept("application/json");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		resultingTimes = resource.type("application/json").put(returnType, timesToSend);
		Assert.assertEquals("Number of times returned", 1, resultingTimes.size());

	}

	@Test
	public void testTimesSyncRanged_attackOtherUser()
	{
		List<Time> timesToSend = new ArrayList<Time>();

		Time newTime = new Time(timeID, start, stop, false, now, task, comment);
		timesToSend.add(newTime);
		resource = resources.client().resource("/sync/times/otherman/100");
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
	public void testTimesSync_attackOtherUser()
	{
		List<Time> timesToSend = new ArrayList<Time>();

		Time newTime = new Time(timeID,  start, stop, false, now, task, comment);
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
		Task otherTask = new Task(UUID.randomUUID(), "d", null, false, now, false, otherUser);
		taskdao.add(otherTask);

		Time newTime = new Time(timeID, start, stop, false, now, otherTask, comment);
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
