package test;

import static org.fest.assertions.api.Assertions.assertThat;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.SyncResource;

import com.sun.jersey.api.client.GenericType;

public class TestSyncResource
{
	private static final String				TESTMAN_ID	= "testman";
	private static EntityManagerFactory		emf			= Persistence.createEntityManagerFactory("test");
	private static UserDAO					userdao		= new UserDAO(emf);
	private static TaskDAO					taskdao		= new TaskDAO(emf);
	private static final User				user		= new User(TESTMAN_ID, TESTMAN_ID, "password", "",
																new ArrayList<Role>());
	private static Task						task		= new Task("123", "Task1", "", false, 0, false, user);

	private static GenericType<List<Task>>	returnType	= new GenericType<List<Task>>()
														{
														};

	@ClassRule
	public static final ResourceTestRule	resources	= ResourceTestRule.builder().addResource(new SyncResource(emf))
																.build();

	@Before
	public void setUp()
	{
		User u = userdao.getUser(TESTMAN_ID);
		if (u == null)
		{
			userdao.add(user);
			taskdao.add(task);
		}
	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void testTaskGet()
	{
		String path = "/sync/task/" + task.getID();
		Task resultingTask = resources.client().resource(path).get(Task.class);
		assertThat(resultingTask.equals(task));
	}

	@Test
	public void testTasksGet()
	{
		List<Task> resultingTasks = resources.client().resource("/sync/tasks/testman").accept("application/json")
				.get(returnType);
		assertThat(resultingTasks.size()).isEqualTo(1);
		Task resultingTask = resultingTasks.get(0);
		assertThat(resultingTask.equals(task));
	}
	/*
		@Test
		public void testTasksSync()
		{
			List<Task> tasksToSend = new ArrayList<Task>();
			Task newTask = new Task("1", "newTask", "", false, 0, false, user);
			tasksToSend.add(newTask);
			List<Task> resultingTasks = resources.client().resource("/sync/tasks/testman").accept("application/json")
					.type("application/json")
					.put(returnType, tasksToSend);
			assertThat(resultingTasks.size()).isEqualTo(2);
		}
		*/
}
