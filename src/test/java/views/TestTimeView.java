package views;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.EditTimeView;

public class TestTimeView
{
	private static EntityManagerFactory emf       = Persistence.createEntityManagerFactory("test");
	private static User                 user;
	private static Time                 time;
	private static UUID                 timeID    = UUID.randomUUID();
	private static UUID                 parentID  = UUID.fromString("e00b8d6f-3f89-4748-98ca-25ef6225d06a");
	private static UUID                 childID   = UUID.fromString("4afe7048-fefe-4c5f-b32f-d4a771175b70");
	private final  HttpSession          session   = Mockito.mock(HttpSession.class);

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		UserDAO userdao = new UserDAO(emf);
		TaskDAO taskDAO = new TaskDAO(emf);
		user = new User("minion", "Do Er", "password", "email", null);
		userdao.add(user);
		
		ZonedDateTime now   = ZonedDateTime.now();
		ZoneId zone = ZonedDateTime.now().getZone();
		ZonedDateTime start = Instant.ofEpochSecond(0).atZone(zone);
		ZonedDateTime stop  = Instant.ofEpochSecond(1).atZone(zone);

		Task    parent  = new Task(parentID, "parent", null, false, now, false, user);
		Task    child   = new Task(childID, "child", parent, false, now, false, user);
		taskDAO.add(parent);
		taskDAO.add(child);


		time = new Time(timeID, start, stop, false, now, parent);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetTime()
	{
		EditTimeView view = new EditTimeView(emf, time, user, null, session);
		Assert.assertEquals(time, view.getTime());
	}

	@Test
	public final void testGetParents() throws SQLException
	{
		EditTimeView view = new EditTimeView(emf, time, user, null, session);
		String expected = "parent";
		String actual = view.getTaskName();
		Assert.assertEquals(expected, actual);
	}

}
