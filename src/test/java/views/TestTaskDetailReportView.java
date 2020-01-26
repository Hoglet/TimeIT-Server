package views;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
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
import se.solit.timeit.views.DayReportView;
import se.solit.timeit.views.TaskDetailReportView;

public class TestTaskDetailReportView
{
	private static EntityManagerFactory  emf  = Persistence.createEntityManagerFactory("test");

	private static User           user;
	private static ZonedDateTime  pointInMonth;
	private static int            dayToTest;
	private static Task           task;

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		pointInMonth = ZonedDateTime.of(2014, 1, dayToTest, 0, 0, 0, 0, ZoneId.of("UTC"));

		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		UUID taskID = UUID.randomUUID();
		UUID taskID2 = UUID.randomUUID();
		UUID timeID = UUID.randomUUID();
		UUID timeID2 = UUID.randomUUID();
		UUID timeID3 = UUID.randomUUID();
		UUID timeID4 = UUID.randomUUID();

		task = new Task(taskID, "Name", null, false, ZonedDateTime.now(), false, user);
		Task task2 = new Task(taskID2, "Name2", null, false, ZonedDateTime.now(), false, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);
		taskdao.add(task2);
		ZonedDateTime start = pointInMonth.withHour(10);
		ZonedDateTime stop = start.plusMinutes(10);
		TimeDAO timeDAO = new TimeDAO(emf);
		Time time = new Time(timeID, start, stop, false, stop, task);
		Time dummyTime = new Time(timeID2, start, start, false, stop, task2);
		Time time2 = new Time(timeID3, start.minusHours(5), start.minusHours(3), false, stop, task);
		Time time3 = new Time(timeID4, start.withHour(15), start.withHour(16), false, stop, task);
		timeDAO.add(time);
		timeDAO.add(time2);
		timeDAO.add(time3);
		timeDAO.add(dummyTime);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	private final HttpSession	session	= Mockito.mock(HttpSession.class);

	@Test
	public final void testGetDay() throws SQLException
	{
		TaskDetailReportView view = new TaskDetailReportView(emf, pointInMonth, user, user, task.getID().toString(),
				null, session);
		Assert.assertEquals("Sat", view.getDay());
	}

	@Test
	public final void testGetMonth() throws SQLException
	{
		TaskDetailReportView view = new TaskDetailReportView(emf, pointInMonth, user, user, task.getID().toString(),
				null, session);
		Assert.assertEquals("January", view.getMonth());
	}

	@Test
	public final void testGetYear() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("2014", view.getYear());
	}

	@Test
	public final void testGetTimes() throws SQLException
	{
		TaskDetailReportView view = new TaskDetailReportView(emf, pointInMonth, user, user, task.getID().toString(),
				null, session);
		List<Time> result = view.getTimes();
		Assert.assertEquals(3, result.size());
	}

	@Test
	public final void testGetDayOfMonth() throws SQLException
	{
		TaskDetailReportView view = new TaskDetailReportView(emf, pointInMonth, user, user, task.getID().toString(),
				null, session);
		int expected = dayToTest;
		int actual = view.getDayOfmonth();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetTask() throws SQLException
	{
		TaskDetailReportView view = new TaskDetailReportView(emf, pointInMonth, user, user, task.getID().toString(),
				null, session);
		String actual = view.getTaskName();
		Assert.assertEquals("Name", actual);
	}

}
