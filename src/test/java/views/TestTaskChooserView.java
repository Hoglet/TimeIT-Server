package views;

import java.sql.SQLException;
import java.util.Date;

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
import se.solit.timeit.views.Action;
import se.solit.timeit.views.TaskChooserView;

public class TestTaskChooserView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static User					user2;

	@BeforeClass
	public static void beforeClass()
	{
		user2 = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user2);
		Task parent = new Task("TaskID-parent", "Parent", null, false, new Date(), false, user2);
		Task child = new Task("TaskID-child", "child", parent, false, new Date(), false, user2);
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
		TaskChooserView view = new TaskChooserView(emf, user2, Action.EDIT);
		Assert.assertEquals(user2, view.getCurrentUser());
	}

	@Test
	public final void testGetParents() throws SQLException
	{
		TaskChooserView view = new TaskChooserView(emf, user2, Action.EDIT);
		Assert.assertEquals("[TaskID-parent=Parent, TaskID-child=Parent/child]", view.getTasks().toString());
	}

}
