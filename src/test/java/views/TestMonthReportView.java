package views;

import java.sql.SQLException;
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
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.MonthReportView;

public class TestMonthReportView
{
	private static EntityManagerFactory  emf     = Persistence.createEntityManagerFactory("test");
	HttpSession                          session = Mockito.mock(HttpSession.class);

	private static User                  user;
	private static ZonedDateTime         pointInMonth;
	private static int                   dayToTest;
	private static ZonedDateTime         now;

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		pointInMonth = ZonedDateTime.of(2014, 1, dayToTest, 0, 0, 0, 0, ZoneId.of("UTC"));
		now = ZonedDateTime.now();

		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		UUID taskID = UUID.randomUUID();
		UUID timeID = UUID.randomUUID();

		Task task = new Task(taskID, "Name", null, false, ZonedDateTime.now(), false, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);
		ZonedDateTime start = pointInMonth.withHour(10);
		ZonedDateTime stop = start.plusMinutes(10);
		Time time = new Time(timeID, start, stop, false, stop, task);
		TimeDAO timeDAO = new TimeDAO(emf);
		timeDAO.add(time);

	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetDay()
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("Wed", view.getDay(1));
	}

	@Test
	public final void testGetMonth()
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("January", view.getMonth());
	}

	@Test
	public final void testGetYear()
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("2014", view.getYear());
	}

	@Test
	public final void testGetDaysInMonth()
	{
		ZonedDateTime pointInMonth2 = ZonedDateTime.of(2015, 2, 11, 0, 0, 0, 0, ZoneId.of("UTC"));
		MonthReportView view = new MonthReportView(emf, pointInMonth2, user, user, null, session);
		Assert.assertEquals(28, view.getDaysInMonth());
	}

	@Test
	public final void testGetDaysInMonth_leapYear()
	{
		ZonedDateTime pointInMonth2 = ZonedDateTime.of(2016, 2, 11, 0, 0, 0, 0, ZoneId.of("UTC"));
		MonthReportView view = new MonthReportView(emf, pointInMonth2, user, user, null, session);
		Assert.assertEquals(29, view.getDaysInMonth());
	}

	@Test
	public final void testGetTimes() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		String expected = "Name";
		TimeDescriptorList result = view.getTimes(dayToTest);
		Assert.assertEquals(1, result.size());
		String actual = result.get(0).getTask().getName();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextMonth() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, now, user, user, null, session);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextMonthLink();

		Assert.assertEquals(expected, actual);
		view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		expected = "<a href='/report/minion/2014/2'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextMonthLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousMonth() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		String expected = "<a href='/report/minion/2013/12'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousMonthLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextYear() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, now, user, user, null, session);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextYearLink();

		Assert.assertEquals(expected, actual);
		view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		expected = "<a href='/report/minion/2015/1'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextYearLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousYear() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null, session);
		String expected = "<a href='/report/minion/2013/1'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousYearLink();
		Assert.assertEquals(expected, actual);
	}

}
