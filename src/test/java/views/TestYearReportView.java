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
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.YearReportView;

public class TestYearReportView
{
	private static EntityManagerFactory	emf		= Persistence.createEntityManagerFactory("test");
	private static User					user;
	private static DateTime				pointInMonth;
	private static int					monthToTest;
	private static int					dayToTest;
	private static DateTime				now;
	private final HttpSession			session	= Mockito.mock(HttpSession.class);

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		monthToTest = 1;
		pointInMonth = new DateTime(2014, monthToTest, dayToTest, 0, 0);
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
	public final void testGetYear()
	{
		YearReportView view = new YearReportView(emf, pointInMonth, user, user, null, session);
		Assert.assertEquals("2014", view.getYear());
	}

	@Test
	public final void testGetTimes() throws SQLException
	{
		YearReportView view = new YearReportView(emf, pointInMonth, user, user, null, session);
		String expected = "Name";
		TimeDescriptorList result = view.getTimes(monthToTest);
		Assert.assertEquals(1, result.size());
		String actual = result.get(0).getTask().getName();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetNextYear() throws SQLException
	{
		YearReportView view = new YearReportView(emf, now, user, user, null, session);
		String expected = "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		String actual = view.getNextYearLink();

		Assert.assertEquals(expected, actual);
		view = new YearReportView(emf, pointInMonth, user, user, null, session);
		expected = "<a href='/report/minion/2015'><button type='button'>&gt;&gt;</button></a>";
		actual = view.getNextYearLink();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public final void testGetPreviousYear() throws SQLException
	{
		YearReportView view = new YearReportView(emf, pointInMonth, user, user, null, session);
		String expected = "<a href='/report/minion/2013'><button type='button'>&lt;&lt;</button></a>";
		String actual = view.getPreviousYearLink();
		Assert.assertEquals(expected, actual);
	}

}
