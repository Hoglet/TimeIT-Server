package se.solit.timeit.views;

import io.dropwizard.jersey.sessions.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class DayReportView extends ReportView
{
	private static final String			DISABLED_FORWARD_BUTTON	= "<button type='button' disabled='disabled'>&gt;&gt;</button>";

	private Map<Integer, List<Time>>	items;

	public DayReportView(EntityManagerFactory emf, DateTime pointInTime, User user, User reportedUser,
			HttpContext context, @Session HttpSession session)
	{
		super("dayReport.ftl", user, pointInTime, reportedUser, context, session, emf);

		DateTime start = pointInTime.withTimeAtStartOfDay();
		DateTime stop = start.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);

		extractTimeDescriptors(start, stop);
		extractTasks();
		extractItems(start, stop);
	}

	private void extractItems(DateTime start, DateTime stop)
	{
		items = new HashMap<Integer, List<Time>>();
		for (int t = 0; t < tasks.size(); t++)
		{
			Task task = tasks.get(t);
			items.put(t, timeDAO.getTimeItems(task, start, stop));
		}
	}

	public String getMonth()
	{
		return pointInTime.toString("MMMMMMMMMM");
	}

	public int getMonthOfYear()
	{
		return pointInTime.getMonthOfYear();
	}

	public String getDay()
	{
		return pointInTime.toString("EEE");
	}

	public int getDayOfmonth()
	{
		return pointInTime.getDayOfMonth();
	}

	public List<Task> getTasks()
	{
		return tasks;
	}

	public int getNumberOfTasks()
	{
		return tasks.size();
	}

	public Task getTask(int column)
	{
		return tasks.get(column);
	}

	public String getCellClass(int hour, int taskColumn)
	{
		if (taskActiveAt(hour, taskColumn))
		{
			return getColumnClass(taskColumn);
		}
		else
		{
			return "";
		}
	}

	private boolean taskActiveAt(int hour, int taskColumn)
	{
		List<Time> timeItems = items.get(taskColumn);
		if (timeItems != null)
		{
			for (Time time : timeItems)
			{
				if (timeIntersectingWithHour(time, hour))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean timeIntersectingWithHour(Time time, int hour)
	{
		int startHour = time.getStart().getHourOfDay();
		int stopHour = time.getStop().getHourOfDay();
		return (startHour == hour || stopHour == hour) ||
				(startHour < hour && stopHour > hour);
	}

	public String getColumnClass(int taskColumn)
	{
		return itemClass[taskColumn];
	}

	public TimeDescriptorList getAllTimes()
	{
		DateTime start = pointInTime.withTimeAtStartOfDay();
		DateTime stop = start.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		return timeDAO.getTimes(user, start, stop);
	}

	public String getPreviousDayLink()
	{
		DateTime nextPointInTime = pointInTime.minusDays(1);
		return createUrl(nextPointInTime, false);
	}

	public String getPreviousMonthLink()
	{
		DateTime nextPointInTime = pointInTime.minusMonths(1);
		return createUrl(nextPointInTime, false);
	}

	public String getPreviousYearLink()
	{
		DateTime nextPointInTime = pointInTime.minusYears(1);
		return createUrl(nextPointInTime, false);
	}

	public String getNextDayLink()
	{
		DateTime nextPointInTime = pointInTime.plusDays(1);
		if (nextPointInTime.isAfterNow())
		{
			return DISABLED_FORWARD_BUTTON;
		}
		else
		{
			return createUrl(nextPointInTime, true);
		}
	}

	public String getNextMonthLink()
	{
		DateTime nextPointInTime = pointInTime.plusMonths(1);
		if (nextPointInTime.isAfterNow())
		{
			return DISABLED_FORWARD_BUTTON;
		}
		else
		{
			return createUrl(nextPointInTime, true);
		}
	}

	public String getNextYearLink()
	{
		DateTime nextPointInTime = pointInTime.plusYears(1);
		if (nextPointInTime.isAfterNow())
		{
			return DISABLED_FORWARD_BUTTON;
		}
		else
		{
			return createUrl(nextPointInTime, true);
		}
	}

	public String getUser()
	{
		return reportedUser.getUsername();
	}

	private String createUrl(DateTime nextPointInTime, boolean forward)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<a href='/report/");
		stringBuilder.append(reportedUser.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(nextPointInTime.getYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(nextPointInTime.getMonthOfYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(nextPointInTime.getDayOfMonth()));
		stringBuilder.append("'>");
		String link = stringBuilder.toString();
		if (forward)
		{
			return link + "<button type='button'>&gt;&gt;</button></a>";
		}
		else
		{
			return link + "<button type='button'>&lt;&lt;</button></a>";
		}
	}

}
