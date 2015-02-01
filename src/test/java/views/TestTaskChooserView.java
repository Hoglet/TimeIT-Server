package views;

import java.sql.SQLException;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.Action;
import se.solit.timeit.views.TaskChooserView;

public class TestTaskChooserView
{
	private static EntityManagerFactory	emf			= Persistence.createEntityManagerFactory("test");
	private static User					user2;
	private static UUID					parentID	= UUID.fromString("24765cb7-d346-4ec4-8fc8-c381b6b38a6e");
	private static UUID					childID		= UUID.fromString("b0d3462a-d214-4969-98f0-1f90e8650cc7");
	private final HttpSession			session		= Mockito.mock(HttpSession.class);

	@BeforeClass
	public static void beforeClass()
	{
		user2 = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user2);
		Task parent = new Task(parentID, "Parent", null, false, DateTime.now(), false, user2);
		Task child = new Task(childID, "child", parent, false, DateTime.now(), false, user2);
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
	public final void testGetParents() throws SQLException
	{
		TaskChooserView view = new TaskChooserView(emf, user2, Action.EDIT, null, session);
		String expected = "[24765cb7-d346-4ec4-8fc8-c381b6b38a6e=Parent, b0d3462a-d214-4969-98f0-1f90e8650cc7=Parent/child]";
		Assert.assertEquals(expected, view.getTasks().toString());
	}

}
