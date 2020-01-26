package se.solit.timeit.views;

import io.dropwizard.jersey.sessions.Session;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class TaskDetailReportView extends ReportView
{
	private List<Time>	items;
	private final Task	task;
	private String		total;

	public TaskDetailReportView(EntityManagerFactory emf, ZonedDateTime pointInTime, User user, User reportedUser,
			String taskid,
			HttpContext context, @Session HttpSession session)
	{
		super("taskDetailReport.ftl", user, pointInTime, reportedUser, context, session, emf);

		ZonedDateTime start = pointInTime.with(LocalTime.MIN);
		ZonedDateTime stop = start.with(LocalTime.MAX);

		TaskDAO taskdao = new TaskDAO(emf);
		task = taskdao.getByID(taskid);
		extractItems(start, stop);
	}

	private void extractItems(ZonedDateTime start, ZonedDateTime stop)
	{
		items = timeDAO.getTimeItems(task, start, stop);
		Duration totalDuration = Duration.ofSeconds(0);
		for (Time time : items)
		{
			totalDuration = totalDuration.plus(Duration.between(time.getStart(), time.getStop()));
		}
		
		long hours = totalDuration.getSeconds() / 3600;
		long minutes = totalDuration.minusHours(hours).getSeconds() / 60;

		total = String.format("%02d:%02d", hours, minutes);
	}

	public String getMonth()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
		return pointInTime.format(formatter);
	}

	public String getDay()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
		return pointInTime.format(formatter);
	}

	public int getDayOfmonth()
	{
		return pointInTime.getDayOfMonth();
	}

	public List<Time> getTimes()
	{
		return items;
	}

	public String getTaskName()
	{
		return task.getName();
	}

	public String getTotal()
	{
		return total;
	}

}
