package views;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
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
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.DayReportView;

public class TestDayReportView
{
	private static EntityManagerFactory  emf = Persistence.createEntityManagerFactory("test");

	private static User                  user;
	private static ZonedDateTime         pointInMonth;
	private static int                   dayToTest;
	private static ZonedDateTime         now;
	private static Task                  task;
	private final static String          comment = "Just a comment";

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		Locale.setDefault(new Locale("en", "UK"));
		ZoneId zone = ZonedDateTime.now().getZone();
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		pointInMonth = ZonedDateTime.of(2014, 1, dayToTest, 0, 0, 0, 0, zone);
		now = ZonedDateTime.now();
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		UUID taskID = UUID.randomUUID();
		UUID taskID2 = UUID.randomUUID();
		UUID timeID = UUID.randomUUID();
		UUID timeID2 = UUID.randomUUID();
		UUID timeID3 = UUID.randomUUID();
		UUID timeID4 = UUID.randomUUID();

		task = new Task(taskID, "Name", null, user);
		Task task2 = new Task(taskID2, "Name2", null, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);
		taskdao.add(task2);
		Instant start = pointInMonth.withHour(10).toInstant();
		Instant stop = start.plusSeconds(600);
		TimeDAO timeDAO = new TimeDAO(emf);
		Time time = new Time(timeID, start, stop, false, stop, task, comment);
		Time dummyTime = new Time(timeID2, start, start, false, stop, task2, comment);
		Time time2 = new Time(timeID3, start.minusSeconds(5*60*60), start.minusSeconds(3*60*60), false, stop, task, comment);
		Time time3 = new Time(timeID4, pointInMonth.withHour(15).toInstant(), pointInMonth.withHour(16).toInstant(), false, stop, task, comment);
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
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("Sat", view.getDay());
	}

	@Test
	public final void testGetMonth() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("january", view.getMonth().toLowerCase());
	}

	@Test
	public final void testGetMonthOfYear() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals(1, view.getMonthOfYear());
	}

	@Test
	public final void testGetUser() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("minion", view.getUser());
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
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		TimeDescriptorList result = view.getAllTimes();
		Assert.assertEquals(2, result.size());
	}

	@Test
	public final void testGetNextDay() throws SQLException
	{
		DayReportView view = new DayReportView(emf, now, user, user, null, session);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextDayLink();

		Assert.assertEquals(expected, actual);
		view = new DayReportView(emf, pointInMonth, user, user, null, session);
		expected = "<a href='/report/minion/2014/1/12'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextDayLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextMonth() throws SQLException
	{
		DayReportView view = new DayReportView(emf, now, user, user, null, session);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextMonthLink();

		Assert.assertEquals(expected, actual);
		view = new DayReportView(emf, pointInMonth, user, user, null, session);
		expected = "<a href='/report/minion/2014/2/11'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextMonthLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousDay() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		String expected = "<a href='/report/minion/2014/1/10'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousDayLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousMonth() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		String expected = "<a href='/report/minion/2013/12/11'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousMonthLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextYear() throws SQLException
	{
		DayReportView view = new DayReportView(emf, now, user, user, null, session);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextYearLink();

		Assert.assertEquals(expected, actual);
		view = new DayReportView(emf, pointInMonth, user, user, null, session);
		expected = "<a href='/report/minion/2015/1/11'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextYearLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousYear() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		String expected = "<a href='/report/minion/2013/1/11'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousYearLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetDayOfMonth() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		int expected = dayToTest;
		int actual = view.getDayOfmonth();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNumberOfTasks() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		int expected = 1;
		int actual = view.getNumberOfTasks();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetColumnClass() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		String expected = "Item0";
		String actual = view.getColumnClass(0);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetTask() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		Task actual = view.getTask(0);
		Assert.assertEquals(task, actual);
	}

	@Test
	public final void testGetTasks() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		List<Task> actual = view.getTasks();
		Assert.assertEquals(1, actual.size());
	}

	@Test
	public final void testCellClass() throws SQLException
	{
		DayReportView view = new DayReportView(emf, pointInMonth, user, user, null, session);
		String actual = view.getCellClass(11, 0);
		String expected = "";
		Assert.assertEquals(expected, actual);

		actual = view.getCellClass(10, 0);
		expected = "Item0";
		Assert.assertEquals(expected, actual);

		actual = view.getCellClass(6, 0);
		expected = "Item0";
		Assert.assertEquals(expected, actual);

		actual = view.getCellClass(15, 0);
		expected = "Item0";
		Assert.assertEquals(expected, actual);

		actual = view.getCellClass(16, 0);
		expected = "Item0";
		Assert.assertEquals(expected, actual);

		view = new DayReportView(emf, pointInMonth.plusDays(1), user, user, null, session);
		actual = view.getCellClass(16, 0);
		expected = "";
		Assert.assertEquals(expected, actual);
	}
}
