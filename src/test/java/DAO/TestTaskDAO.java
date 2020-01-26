package DAO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTaskDAO
{
	private static final UUID           childID       = UUID.randomUUID();
	private static final UUID           parentID      = UUID.randomUUID();
	private final UUID                  grandchildID  = UUID.randomUUID();
	private static EntityManagerFactory emf           = Persistence.createEntityManagerFactory("test");
	private static UserDAO              userdao       = new UserDAO(emf);
	private static TaskDAO              taskdao       = new TaskDAO(emf);
	private static User                 user          = new User("testman", "Test Tester", "password", "",
																new ArrayList<Role>());
	private final EntityManager         em            = emf.createEntityManager();
	private final ZoneId                zone          = ZonedDateTime.now().getZone();
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
		deleteAllTasks();
		deleteAllTasks();
	}

	private void deleteAllTasks()
	{
		em.getTransaction().begin();
		Query query = em.createQuery("DELETE FROM Task t");
		query.executeUpdate();
		em.getTransaction().commit();
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testUpdate() throws SQLException
	{
		Task task = new Task(parentID, "Task1", null, false, ZonedDateTime.now().minusSeconds(3), false, user);
		taskdao.add(task);
		Task task2 = taskdao.getByID(task.getID());
		task2.setName("Tjohopp");
		assertFalse(task2.equals(task));
		taskdao.update(task2);
		Task task3 = taskdao.getByID(task.getID());
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
	public final void testUpdate_recursiveProtection() throws SQLException
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);
		Task grandchild = new Task(grandchildID, "grandchild", child, false, ZonedDateTime.now(), false, user);
		taskdao.add(parent);
		taskdao.add(child);
		taskdao.add(grandchild);
		parent.setParent(grandchild);

		taskdao.update(parent);
		child = taskdao.getByID(child.getID());
		Assert.assertEquals(null, child.getParent());
	}

	@Test
	public final void testgetByIDs() throws SQLException
	{
		Task task = new Task(parentID, "Task1", null, false, ZonedDateTime.now(), false, user);
		Collection<Task> resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(resultingTasks.size(), 0);
		taskdao.add(task);
		resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(resultingTasks.size(), 1);

	}

	@Test
	public final void testAddTwice() throws SQLException
	{
		Task task = new Task(parentID, "Task1", null, false, ZonedDateTime.now(), false, user);
		try
		{
			taskdao.add(task);
			taskdao.add(task);
			assertTrue("Should not allow adding twice", false);
		}
		catch (Exception e)
		{
			Assert.assertEquals(RollbackException.class, e.getClass());
		}
	}

	@Test
	public final void testAddWithoutOwner() throws SQLException
	{
		try
		{
			Task badTask = new Task(parentID, "Task1", null, false, ZonedDateTime.now(), false, null);
			taskdao.add(badTask);
			assertTrue("Should not allow null user", false);
		}
		catch (Exception e)
		{
			Assert.assertEquals(NullPointerException.class, e.getClass());
		}
	}

	@Test
	public final void testUpdateOrAdd() throws SQLException
	{
		Task parent = new Task(parentID, "Parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "Child", parent, false, ZonedDateTime.now(), false, user);
		Task[] tasks = new Task[] { child, parent };
		taskdao.updateOrAdd(tasks);
		assertEquals(2, taskdao.getTasks(user.getUsername()).size());
	}

	@Test
	public final void testUpdateOrAdd_reversedOrder() throws SQLException
	{
		Task parent = new Task(parentID, "Parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "Child", parent, false, ZonedDateTime.now(), false, user);
		Task[] tasks = new Task[] { child, parent };
		taskdao.updateOrAdd(tasks);
		assertEquals(2, taskdao.getTasks(user.getUsername()).size());
	}

	@Test
	public final void testUpdateOrAdd_change() throws SQLException
	{
		Task task = new Task(parentID, "Task1", null, false, ZonedDateTime.now().minusSeconds(3), false, user);
		Task[] tasks = new Task[] { task };
		taskdao.add(task);
		task.setName("TWo");
		tasks = new Task[] { task };
		taskdao.updateOrAdd(tasks);
		Collection<Task> resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(1, resultingTasks.size());
		Task t2 = resultingTasks.iterator().next();
		assertTrue(t2.getName().equals("TWo"));
	}

	@Test
	public final void testUpdateOrAdd_noChangeIfOlder() throws SQLException
	{
		ZonedDateTime now = ZonedDateTime.now();
		ZonedDateTime then = Instant.ofEpochSecond(now.toInstant().getEpochSecond() - 1).atZone(zone);
		Task task = new Task(parentID, "Task1", null, false, now, false, user);
		Task[] tasks = new Task[] { task };
		taskdao.add(task);

		task = new Task(parentID, "Task2", null, false, then, false, user);
		tasks = new Task[] { task };
		taskdao.updateOrAdd(tasks);
		Collection<Task> resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(resultingTasks.size(), 1);
		Task t2 = resultingTasks.iterator().next();
		assertTrue(t2.getName().equals("Task1"));
	}

	@Test
	public final void testUpdateOrAdd_noDifferense() throws SQLException
	{
		Task task = new Task(parentID, "Task1", null, false, ZonedDateTime.now(), false, user);
		Task[] tasks = new Task[] { task };
		taskdao.updateOrAdd(tasks);
		taskdao.updateOrAdd(tasks);
	}

	@Test
	public final void testUpdateOrAdd_badData() throws SQLException
	{
		Task parent = new Task(parentID, "Parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "Child", parent, false, ZonedDateTime.now(), false, user);
		Task[] tasks = new Task[] { child };
		taskdao.updateOrAdd(tasks);
		assertEquals(0, taskdao.getTasks(user.getUsername()).size());
	}

	@Test
	public final void testgetByID()
	{
		Task task = new Task(parentID, "Task1", null, false, ZonedDateTime.now(), false, user);
		Task resultingTask = taskdao.getByID(task.getID());
		assertEquals(resultingTask, null);
		taskdao.add(task);
		resultingTask = taskdao.getByID(task.getID());
		assertTrue(task.equals(resultingTask));
	}

	@Test
	public final void testgetByID_parent()
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);
		List<Task> resultingTasks = taskdao.getTasks(user.getUsername(), null, false);
		assertEquals(0, resultingTasks.size());
		taskdao.add(parent);
		taskdao.add(child);
		resultingTasks = taskdao.getTasks(user.getUsername(), null, false);
		assertEquals(1, resultingTasks.size());
	}

	@Test
	public final void testgetByID_child()
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);
		List<Task> resultingTasks = taskdao.getTasks(user.getUsername(), null, false);
		assertEquals(0, resultingTasks.size());
		taskdao.add(parent);
		taskdao.add(child);
		resultingTasks = taskdao.getTasks(user.getUsername(), parent, false);
		assertEquals(1, resultingTasks.size());
	}

	@Test
	public final void testDelete()
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);
		List<Task> resultingTasks = taskdao.getTasks(user.getUsername(), null, false);
		assertEquals(0, resultingTasks.size());
		taskdao.add(parent);
		taskdao.add(child);
		taskdao.delete(child);
		resultingTasks = taskdao.getTasks(user.getUsername(), parent, false);
		assertEquals(0, resultingTasks.size());

		taskdao.delete(parent);
		Task actualChild = taskdao.getByID(child.getID());
		assertEquals(parent, actualChild.getParent());
	}

	@Test
	public final void testDelete2()
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);

		taskdao.add(parent);
		taskdao.add(child);
		taskdao.delete(parent);
		Task actualChild = taskdao.getByID(child.getID());
		assertEquals(null, actualChild.getParent());
	}

	@Test
	public final void testDeleteParent()
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);
		List<Task> resultingTasks = taskdao.getTasks(user.getUsername(), null, false);
		assertEquals(0, resultingTasks.size());
		taskdao.add(parent);
		taskdao.add(child);
		taskdao.delete(parent);
		resultingTasks = taskdao.getTasks(user.getUsername(), null, false);
		assertEquals(1, resultingTasks.size());
		assertEquals(childID, resultingTasks.get(0).getID());
	}

	@Test
	public final void tesrGetTasks() throws SQLException
	{
		Task parent = new Task(parentID, "parent", null, false, ZonedDateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, ZonedDateTime.now(), false, user);
		List<Task> resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(0, resultingTasks.size());
		taskdao.add(parent);
		taskdao.add(child);
		taskdao.delete(parent);
		resultingTasks = taskdao.getTasks(user.getUsername());
		assertEquals(1, resultingTasks.size());
		assertEquals(childID, resultingTasks.get(0).getID());
	}

}
