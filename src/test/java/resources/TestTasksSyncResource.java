package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;

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
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TasksSyncResource;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class TestTasksSyncResource
{

	private static final UUID            childID     = UUID.randomUUID();
	private static final UUID            parentID    = UUID.randomUUID();
	private static final String          TESTMAN_ID  = "testman";
	private static EntityManagerFactory  emf         = Persistence.createEntityManagerFactory("test");
	private static UserDAO               userdao     = new UserDAO(emf);
	private static TaskDAO               taskdao     = new TaskDAO(emf);
	private static User                  user;
	private static Task                  task;

	private static GenericType<List<Task>>  returnType  = new GenericType<List<Task>>()
															{
															};
	private static BasicAuthProvider<User>  myAuthenticator = new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	@ClassRule
	public static final ResourceTestRule  resources  = ResourceTestRule.builder()
	                                                                   .addProvider(
	                                                                           new ContextInjectableProvider<HttpHeaders>(
	                                                                             HttpHeaders.class, null))
	                                                                   .addResource(myAuthenticator)
	                                                                   .addResource(new TasksSyncResource(emf)).build();

	@BeforeClass
	public static void beforeClass()
	{
		user = new User(TESTMAN_ID, TESTMAN_ID, "password", "", new ArrayList<Role>());
		userdao.add(user);
		task = new Task(UUID.randomUUID(), "Task1", null, false, false, user);
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
		TypedQuery<Task> getQuery = em.createQuery("SELECT t FROM Task t", Task.class);
		List<Task> tasks = getQuery.getResultList();
		for (Task task : tasks)
		{
			em.remove(task);
		}
		em.getTransaction().commit();
		// em.close();
	}

	@Test
	public void testTasksGet()
	{
		taskdao.add(task);
		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		List<Task> resultingTasks = resource.accept("application/json").get(returnType);

		Assert.assertEquals(1, resultingTasks.size());
		Task resultingTask = resultingTasks.get(0);
		Assert.assertTrue(resultingTask.equals(task));
	}

	@Test
	public void testTasksGet_deleted()
	{
		Task task2 = new Task(UUID.randomUUID(), "Task1", null, false, true, user);
		taskdao.add(task2);
		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		List<Task> resultingTasks = resource.accept("application/json").get(returnType);

		Assert.assertEquals(1, resultingTasks.size());
		Task resultingTask = resultingTasks.get(0);
		Assert.assertTrue(resultingTask.equals(task2));
	}

	@Test
	public void testTasksSync()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task newTask = new Task(parentID, "newTask", null, false, false, user);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 1, resultingTasks.size());
	}

	@Test
	public void testTasksSync_deleted()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task newTask = new Task(parentID, "newTask", null, false, true, user);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 1, resultingTasks.size());
	}

	@Test
	public void testTasksSync_invalidDataAttack()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		User otherUser = new User("innocent", "bystander", "unkown", "", null);
		Task newTask = new Task(parentID, "newTask", null, false, false, otherUser);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.accept("application/json").type("application/json").put(returnType, tasksToSend);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testTasksSync_accessingOtherUser()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task newTask = new Task(parentID, "newTask", null, false, false, user);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/otherman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.accept("application/json").type("application/json").put(returnType, tasksToSend);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testTasksSync_advanced1()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task parent = new Task(parentID, "parent", null, false, false, user);
		Task child = new Task(childID, "child", parent, false, false, user);
		tasksToSend.add(child);
		tasksToSend.add(parent);

		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 2, resultingTasks.size());
	}

	@Test
	public void testTasksSync_advanced2()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task parent = new Task(parentID, "parent", null, false, false, user);
		Task child = new Task(childID, "child", parent, false, false, user);
		tasksToSend.add(parent);
		tasksToSend.add(child);

		WebResource resource = resources.client().resource("/sync/tasks/testman");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 2, resultingTasks.size());
	}

	/**/
	@Test
	public void testTasksSyncRanged()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Instant changeTime2 = Instant.ofEpochSecond(100);
		Task newTask = new Task(parentID, "newTask", null, false, changeTime2, false, user);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/testman/100");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 1, resultingTasks.size());

		resource = resources.client().resource("/sync/tasks/testman/101");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 0, resultingTasks.size());

	}

	@Test
	public void testTasksSyncRanged_deleted()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task newTask = new Task(parentID, "newTask", null, false, true, user);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/testman/0");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 1, resultingTasks.size());
	}

	@Test
	public void testTasksSyncRanged_invalidDataAttack()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		User otherUser = new User("innocent", "bystander", "unkown", "", null);
		Task newTask = new Task(parentID, "newTask", null, false, false, otherUser);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/testman/0");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.accept("application/json").type("application/json").put(returnType, tasksToSend);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testTasksSyncRanged_accessingOtherUser()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task newTask = new Task(parentID, "newTask", null, false, false, user);
		tasksToSend.add(newTask);
		WebResource resource = resources.client().resource("/sync/tasks/otherman/0");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));
		try
		{
			resource.accept("application/json").type("application/json").put(returnType, tasksToSend);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testTasksSyncRanged_advanced1()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task parent = new Task(parentID, "parent", null, false, false, user);
		Task child = new Task(childID, "child", parent, false, false, user);
		tasksToSend.add(child);
		tasksToSend.add(parent);

		WebResource resource = resources.client().resource("/sync/tasks/testman/0");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 2, resultingTasks.size());
	}

	@Test
	public void testTasksSyncRanged_advanced2()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task parent = new Task(parentID, "parent", null, false, false, user);
		Task child = new Task(childID, "child", parent, false, false, user);
		tasksToSend.add(parent);
		tasksToSend.add(child);

		WebResource resource = resources.client().resource("/sync/tasks/testman/0");
		resource.addFilter(new HTTPBasicAuthFilter(TESTMAN_ID, "password"));

		List<Task> resultingTasks = resource.accept("application/json").type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 2, resultingTasks.size());
	}

}
