package se.solit.timeit.views;

import io.dropwizard.jersey.sessions.Session;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

	public TaskDetailReportView(EntityManagerFactory emf, DateTime pointInTime, User user, User reportedUser,
			String taskid,
			HttpContext context, @Session HttpSession session)
	{
		super("taskDetailReport.ftl", user, pointInTime, reportedUser, context, session, emf);

		DateTime start = pointInTime.withTimeAtStartOfDay();
		DateTime stop = start.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);

		TaskDAO taskdao = new TaskDAO(emf);
		task = taskdao.getByID(taskid);
		extractItems(start, stop);
	}

	private void extractItems(DateTime start, DateTime stop)
	{
		items = timeDAO.getTimeItems(task, start, stop);
		Duration totalDuration = new Duration(0);
		for (Time time : items)
		{
			totalDuration = totalDuration.plus(new Duration(time.getStart(), time.getStop()));
		}
		PeriodFormatter hoursAndMinutes = new PeriodFormatterBuilder()
				.minimumPrintedDigits(TWO)
				.printZeroAlways()
				.appendHours()
				.appendSeparator(":")
				.appendMinutes()
				.toFormatter();
		Period period = totalDuration.toPeriod();
		total = hoursAndMinutes.print(period);
	}

	public String getMonth()
	{
		return pointInTime.toString("MMMMMMMMMM");
	}

	public String getDay()
	{
		return pointInTime.toString("EEE");
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
