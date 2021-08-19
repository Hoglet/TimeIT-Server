package views;

import java.sql.SQLException;
import java.time.Instant;
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
import se.solit.timeit.views.IndexView;

public class TestIndexView
{
	private static EntityManagerFactory  emf       = Persistence.createEntityManagerFactory("test");
	private static User                  user;
	private static UUID                  parentID  = UUID.fromString("b141b8ff-fa8e-47ff-8631-d86fe97cbc2b");
	private static UUID                  childID   = UUID.fromString("c624ba2d-2027-4858-9696-3efc4e4106ad");
	private static HttpSession           session;
	private final static ZonedDateTime   now       = ZonedDateTime.now();
	private final static String          comment   = "Just a comment";

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		Task parent = new Task(parentID, "Parent", null, user);
		Task child = new Task(childID, "child", parent, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(parent);
		taskdao.add(child);
		Instant start = now.withHour(10).toInstant();
		Instant stop = start.plusSeconds(10 * 60);
		Time time = new Time(UUID.randomUUID(), start, stop, false, now.toInstant(), parent, comment);
		TimeDAO timeDAO = new TimeDAO(emf);
		timeDAO.add(time);
		time = new Time(UUID.randomUUID(), start, stop, false, now.toInstant(), child, comment);
		timeDAO.add(time);

		if (now.getDayOfMonth() != 4)
		{
			start = now.withDayOfMonth(4).toInstant();
		}
		else
		{
			start = now.withDayOfMonth(3).toInstant();
		}
		stop = start.plusSeconds(10 * 60);
		time = new Time(UUID.randomUUID(), start, stop, false, now.toInstant(), child, comment);
		timeDAO.add(time);

		if (now.getMonthValue() != 4)
		{
			start = now.withMonth(4).toInstant();
		}
		else
		{
			start = now.withMonth(3).toInstant();
		}
		stop = start.plusSeconds(10 * 60);
		time = new Time(UUID.randomUUID(), start, stop, false, now.toInstant(), child, comment);
		timeDAO.add(time);

		session = Mockito.mock(HttpSession.class);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetTasks() throws SQLException
	{
		IndexView view = new IndexView(user, emf, null, session);
		String expected = "b141b8ff-fa8e-47ff-8631-d86fe97cbc2b";
		Assert.assertEquals(2, view.getTasks().size());

		Task result = view.getTasks().get(0).getTask();
		Assert.assertEquals(expected, result.getID().toString());
	}

	@Test
	public final void testGetTimes() throws SQLException
	{
		IndexView view = new IndexView(user, emf, null, session);
		String expectedTask = "Parent";
		Assert.assertEquals(1, view.getTodaysTimes().size());
		String result = view.getTodaysTimes().get(0).getTask().getName();
		Assert.assertEquals("TodaysTimes: ", expectedTask, result);
		result = view.getTodaysTimes().get(0).getDurationWithChildrenStringAlways();
		String expectedTime = "00:20";
		Assert.assertEquals("TodaysTimes: ", expectedTime, result);

		result = view.getMonthsTimes().get(0).getTask().getName();
		Assert.assertEquals("MonthsTimes: ", expectedTask, result);
		result = view.getMonthsTimes().get(0).getDurationWithChildrenStringAlways();
		expectedTime = "00:30";
		Assert.assertEquals("MonthsTimes: ", expectedTime, result);

		result = view.getYearsTimes().get(0).getTask().getName();
		Assert.assertEquals("YearsTimes: ", expectedTask, result);
		result = view.getYearsTimes().get(0).getDurationWithChildrenStringAlways();
		expectedTime = "00:40";
		Assert.assertEquals("YearsTimes: ", expectedTime, result);

	}

}
