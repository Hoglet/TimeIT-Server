package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTaskDAO
{
	private static EntityManagerFactory	emf		= Persistence.createEntityManagerFactory("test");
	private static UserDAO				userdao	= new UserDAO(emf);
	private static TaskDAO				taskdao	= new TaskDAO(emf);
	private static User					user	= new User("testman", "Test Tester", "password", "",
														new ArrayList<Role>());
	private static Task					task	= new Task("123", "Task1", "", false, 1000, false, user);
	private final EntityManager			em		= emf.createEntityManager();								;

	@BeforeClass
	public static void beforeClass()
	{
		userdao.add(user);
	}

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception
	{
		em.getTransaction().begin();
		TypedQuery<Task> getQuery = em.createQuery("SELECT t FROM Task t",
				Task.class);
		List<Task> tasks = getQuery.getResultList();
		for (Task task : tasks)
		{
			em.remove(task);
		}
		em.getTransaction().commit();
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

		try
		{
			task3.setOwner(null);
			taskdao.update(task3);
			assertTrue("Should not allow null user", false);
		}
		catch (Exception e)
		{
			// Success
		}

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
	public final void testAddTwice() throws SQLException
	{
		try
		{
			taskdao.add(task);
			taskdao.add(task);
			assertTrue("Should not allow adding twice", false);
		}
		catch (Exception e)
		{
			// Success
		}
	}

	@Test
	public final void testAddWithoutOwner() throws SQLException
	{
		try
		{
			Task badTask = new Task("123", "Task1", "", false, 0, false, null);
			taskdao.add(badTask);
			assertTrue("Should not allow null user", false);
		}
		catch (Exception e)
		{
			// Success
		}
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

		t2.setLastChange(900);
		Task[] tasks2 = new Task[] { task };
		taskdao.updateOrAdd(tasks2);
		Task t3 = taskdao.getTask(task.getID());
		assertEquals("Should not update if change time is older than current", t3, task);

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