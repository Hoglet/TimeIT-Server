package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTaskDAO
{
	private static EntityManagerFactory	emf;
	private static UserDAO				userdao;
	private static TaskDAO				taskdao;
	private static Task					task;
	private static User					user;

	@Before
	public void setUp() throws Exception
	{
		emf = Persistence.createEntityManagerFactory("test");
		userdao = new UserDAO(emf);
		user = new User("testman", "Test Tester", "password", "",
				new ArrayList<Role>());
		userdao.add(user);
		task = new Task("123", "Task1", "", false, 0, false, user);
		taskdao = new TaskDAO(emf);
	}

	@After
	public void tearDown() throws Exception
	{
		emf.close();
	}

	@Test
	public final void testUpdate() throws SQLException
	{
		taskdao.add(task);
		Task task2 = taskdao.getTask(task.getID());
		task2.setName("Tjohopp");
		assertFalse(task2.equals(task));
		taskdao.update(task2);
		Task task3 = taskdao.getTask(task.getID());
		assertEquals(task3.getName(), "Tjohopp");
	}

	@Test
	public final void testGetTasks() throws SQLException
	{
		Collection<Task> resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(resultingTasks.size(), 0);
		taskdao.add(task);
		resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(resultingTasks.size(), 1);

	}

	@Test
	public final void testUpdateOrAdd() throws SQLException
	{
		assertEquals(taskdao.getTasks(user.getUsername()).size(), 0);
		Task[] tasks = new Task[] { task };
		taskdao.updateOrAdd(tasks);
		assertEquals(taskdao.getTasks(user.getUsername()).size(), 1);
		task.setName("TWo");
		taskdao.updateOrAdd(tasks);
		Collection<Task> resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(resultingTasks.size(), 1);
		Task t2 = resultingTasks.iterator().next();
		assertTrue(t2.getName().equals("TWo"));
	}

	@Test
	public final void testGetTask()
	{
		Task resultingTask = taskdao.getTask(task.getID());
		assertEquals(resultingTask, null);
		taskdao.add(task);
		resultingTask = taskdao.getTask(task.getID());
		assertTrue(task.equals(resultingTask));
	}
}
