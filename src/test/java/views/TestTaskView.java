package views;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.TaskView;

public class TestTaskView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static User					user2;
	private static Task					task;

	@BeforeClass
	public static void beforeClass()
	{
		user2 = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user2);
		task = new Task("TaskID", "Name", null, false, 0, false, user2);
		Task parent = new Task("TaskID-parent", "Parent", null, false, 0, false, user2);
		Task child = new Task("TaskID-child", "child", parent, false, 0, false, user2);
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
	public final void testGetCurrentUser()
	{
		TaskView taskView = new TaskView(emf, task, user2);
		Assert.assertEquals(user2, taskView.getCurrentUser());
	}

	@Test
	public final void testGetParents() throws SQLException
	{
		TaskView taskView = new TaskView(emf, task, user2);
		Assert.assertEquals("[TaskID-parent=Parent, TaskID-child=Parent/child]", taskView.getParents().toString());
	}

	@Test
	public final void testGetTask()
	{
		TaskView taskView = new TaskView(emf, task, user2);
		Assert.assertEquals(task, taskView.getTask());
	}

}
