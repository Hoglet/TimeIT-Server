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
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.Action;
import se.solit.timeit.views.TimeView;

public class TestTimeView
{
	private static EntityManagerFactory	emf			= Persistence.createEntityManagerFactory("test");
	private static User					user;
	private static Time					time;
	private static String				timeID		= UUID.randomUUID().toString();
	private static String				parentID	= "e00b8d6f-3f89-4748-98ca-25ef6225d06a";
	private static String				childID		= "4afe7048-fefe-4c5f-b32f-d4a771175b70";

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		DateTime now = DateTime.now();
		Task parent = new Task(parentID, "parent", null, false, now, false, user);
		Task child = new Task(childID, "child", parent, false, now, false, user);
		TaskDAO taskDAO = new TaskDAO(emf);
		taskDAO.add(parent);
		taskDAO.add(child);

		time = new Time(timeID, new DateTime(0), new DateTime(1000), false, now, parent);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetCurrentUser()
	{
		TimeView view = new TimeView(emf, time, user, Action.ADD);
		Assert.assertEquals(user, view.getCurrentUser());
	}

	@Test
	public final void testGetTime()
	{
		TimeView view = new TimeView(emf, time, user, Action.ADD);
		Assert.assertEquals(time, view.getTime());
	}

	@Test
	public final void testGetParents() throws SQLException
	{
		TimeView view = new TimeView(emf, time, user, Action.ADD);
		String expected = "[e00b8d6f-3f89-4748-98ca-25ef6225d06a=parent, 4afe7048-fefe-4c5f-b32f-d4a771175b70=parent/child]";
		String actual = view.getTasks().toString();
		Assert.assertEquals(expected, actual);
	}

}
