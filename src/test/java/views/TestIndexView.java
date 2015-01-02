package views;

import java.sql.SQLException;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.IndexView;

public class TestIndexView
{
	private static EntityManagerFactory	emf			= Persistence.createEntityManagerFactory("test");
	private static User					user;
	private static UUID					taskID		= UUID.randomUUID();
	private static UUID					parentID	= UUID.fromString("b141b8ff-fa8e-47ff-8631-d86fe97cbc2b");
	private static UUID					childID		= UUID.fromString("c624ba2d-2027-4858-9696-3efc4e4106ad");
	private static Task					task;

	@BeforeClass
	public static void beforeClass()
	{
		user = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		task = new Task(taskID, "Name", null, false, DateTime.now(), false, user);
		Task parent = new Task(parentID, "Parent", null, false, DateTime.now(), false, user);
		Task child = new Task(childID, "child", parent, false, DateTime.now(), false, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(parent);
		taskdao.add(child);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetTasks() throws SQLException
	{
		IndexView view = new IndexView(user, emf);
		String expected = "b141b8ff-fa8e-47ff-8631-d86fe97cbc2b";
		Assert.assertEquals(1, view.getTasks().size());
		Task result = view.getTasks().get(0).getKey();
		Assert.assertEquals(expected, result.getID().toString());
	}
}
