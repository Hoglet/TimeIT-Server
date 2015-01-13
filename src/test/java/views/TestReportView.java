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
import se.solit.timeit.views.ReportView;

public class TestReportView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static User					user;
	private static DateTime				pointInMonth;
	private static int					dayToTest;

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		pointInMonth = new DateTime(2014, 1, dayToTest, 0, 0);

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
		ReportView view = new ReportView("monthReport.ftl", user, pointInMonth, user);
		Assert.assertEquals("2014", view.getYear());
	}

	@Test
	public final void testGetTabs()
	{
		ReportView view = new ReportView("monthReport.ftl", user, pointInMonth, user);
		Assert.assertTrue(view.tabs(0).contains("<div class='tab selected'><h1>Month "));
		Assert.assertTrue(view.tabs(1).contains("<div class='tab selected'><h1>Year "));
	}

}
