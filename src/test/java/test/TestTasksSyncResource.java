package test;

import io.dropwizard.testing.junit.ResourceTestRule;

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
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TasksSyncResource;

import com.sun.jersey.api.client.GenericType;

public class TestTasksSyncResource
{

	private static final String				TESTMAN_ID	= "testman";
	private static EntityManagerFactory		emf			= Persistence.createEntityManagerFactory("test");
	private static UserDAO					userdao		= new UserDAO(emf);
	private static TaskDAO					taskdao		= new TaskDAO(emf);
	private static User						user;
	private static Task						task;

	private static GenericType<List<Task>>	returnType	= new GenericType<List<Task>>()
														{
														};

	@ClassRule
	public static final ResourceTestRule	resources	= ResourceTestRule.builder()
																.addResource(new TasksSyncResource(emf))
																.build();

	@BeforeClass
	public static void beforeClass()
	{
		user = new User(TESTMAN_ID, TESTMAN_ID, "password", "", new ArrayList<Role>());
		userdao.add(user);
		task = new Task("123", "Task1", "", false, 0, false, user);
	}

	@AfterClass
	public static void afterClass()
	{
		userdao.delete(user);
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
		TypedQuery<Task> getQuery = em.createQuery("SELECT t FROM Task t",
				Task.class);
		List<Task> tasks = getQuery.getResultList();
		for (Task task : tasks)
		{
			em.remove(task);
		}
		em.getTransaction().commit();
		//		em.close();
	}

	@Test
	public void testTasksGet()
	{
		taskdao.add(task);
		List<Task> resultingTasks = resources.client().resource("/sync/tasks/testman").accept("application/json")
				.get(returnType);
		Assert.assertEquals(resultingTasks.size(), 1);
		Task resultingTask = resultingTasks.get(0);
		Assert.assertTrue(resultingTask.equals(task));
	}

	@Test
	public void testTasksSync()
	{
		List<Task> tasksToSend = new ArrayList<Task>();
		Task newTask = new Task("1", "newTask", "", false, 0, false, user);
		tasksToSend.add(newTask);
		List<Task> resultingTasks = resources.client().resource("/sync/tasks/testman").accept("application/json")
				.type("application/json")
				.put(returnType, tasksToSend);
		Assert.assertEquals("Number of tasks returned", 1, resultingTasks.size());
	}
}
