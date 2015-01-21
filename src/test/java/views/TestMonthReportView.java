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
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.MonthReportView;

public class TestMonthReportView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static User					user;
	private static DateTime				pointInMonth;
	private static int					dayToTest;
	private static DateTime				now;

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		pointInMonth = new DateTime(2014, 1, dayToTest, 0, 0);
		now = DateTime.now();

		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		UUID taskID = UUID.randomUUID();
		UUID timeID = UUID.randomUUID();

		Task task = new Task(taskID, "Name", null, false, DateTime.now(), false, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);
		DateTime start = pointInMonth.withHourOfDay(10);
		DateTime stop = start.plusMinutes(10);
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
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null);
		Assert.assertEquals("Wed", view.getDay(1));
	}

	@Test
	public final void testGetMonth()
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null);
		Assert.assertEquals("January", view.getMonth());
	}

	@Test
	public final void testGetYear()
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null);
		Assert.assertEquals("2014", view.getYear());
	}

	@Test
	public final void testGetDaysInMonth()
	{
		DateTime pointInMonth2 = new DateTime(2015, 2, 11, 0, 0);
		MonthReportView view = new MonthReportView(emf, pointInMonth2, user, user, null);
		Assert.assertEquals(28, view.getDaysInMonth());
	}

	@Test
	public final void testGetDaysInMonth_leapYear()
	{
		DateTime pointInMonth2 = new DateTime(2016, 2, 11, 0, 0);
		MonthReportView view = new MonthReportView(emf, pointInMonth2, user, user, null);
		Assert.assertEquals(29, view.getDaysInMonth());
	}

	@Test
	public final void testGetTimes() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null);
		String expected = "Name";
		TimeDescriptorList result = view.getTimes(dayToTest);
		Assert.assertEquals(1, result.size());
		String actual = result.get(0).getTask().getName();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextMonth() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, now, user, user, null);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextMonthLink();

		Assert.assertEquals(expected, actual);
		view = new MonthReportView(emf, pointInMonth, user, user, null);
		expected = "<a href='/report/minion/2014/2'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextMonthLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousMonth() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null);
		String expected = "<a href='/report/minion/2013/12'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousMonthLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextYear() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, now, user, user, null);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextYearLink();

		Assert.assertEquals(expected, actual);
		view = new MonthReportView(emf, pointInMonth, user, user, null);
		expected = "<a href='/report/minion/2015/1'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextYearLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousYear() throws SQLException
	{
		MonthReportView view = new MonthReportView(emf, pointInMonth, user, user, null);
		String expected = "<a href='/report/minion/2013/1'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousYearLink();
		Assert.assertEquals(expected, actual);
	}

}
