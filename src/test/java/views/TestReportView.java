package views;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
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
import se.solit.timeit.views.ReportView;

public class TestReportView
{
	private final HttpSession            session  = Mockito.mock(HttpSession.class);
	private static EntityManagerFactory  emf      = Persistence.createEntityManagerFactory("test");

	private static User           user;
	private static ZonedDateTime  pointInMonth;
	private static int            dayToTest;
	private static Task           task;
	private final static String   comment       = "Just a comment";

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		dayToTest = 11;
		pointInMonth = ZonedDateTime.of(2014, 1, dayToTest, 0, 0, 0, 0, ZoneId.of("UTC"));

		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
		UUID taskID = UUID.randomUUID();
		UUID timeID = UUID.randomUUID();

		task = new Task(taskID, "Name", null, user);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);
		Instant start = pointInMonth.withHour(10).toInstant();
		Instant stop = start.plusSeconds(10 * 60);
		Time time = new Time(timeID, start, stop, false, stop, task, comment);
		TimeDAO timeDAO = new TimeDAO(emf);
		timeDAO.add(time);

	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetTaskClass()
	{
		ReportView view = new ReportView("monthReport.ftl", user, pointInMonth, user, null, session, emf);

		ZonedDateTime start = pointInMonth.with(LocalTime.MIN);
		ZonedDateTime stop = start.withHour(23);
		view.extractTimeDescriptors(start, stop);
		view.extractTasks();
		Assert.assertEquals("Item Item0", view.getTaskClass(task));
		Task unknownTask = new Task(UUID.randomUUID(), "Who", null, user);
		Assert.assertEquals("Item ", view.getTaskClass(unknownTask));
	}

	@Test
	public final void testGetYear()
	{
		ReportView view = new ReportView("monthReport.ftl", user, pointInMonth, user, null, session, emf);
		Assert.assertEquals("2014", view.getYear());
	}

	@Test
	public final void testGetTabs()
	{
		ReportView view = new ReportView("monthReport.ftl", user, pointInMonth, user, null, session, emf);
		String tab1 = view.tabs(0);
		String tab2 = view.tabs(1);
		String tab3 = view.tabs(2);
		String tab4 = view.tabs(3);
		Assert.assertTrue(tab1.contains("<div class='tab selected'><h2>Year"));
		Assert.assertTrue(tab2.contains("<div class='tab selected'><h2>Month"));
		Assert.assertTrue(tab3.contains("<div class='tab selected'><h2>Day"));
		Assert.assertTrue(tab4.contains("<div class='tab selected'><h2>Details"));
	}

}
