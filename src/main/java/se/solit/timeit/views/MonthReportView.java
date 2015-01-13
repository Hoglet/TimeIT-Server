package se.solit.timeit.views;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class MonthReportView extends ReportView
{
	private final TimeDAO	timeDAO;

	public MonthReportView(EntityManagerFactory emf, DateTime pointInMonth, User user, User reportedUser)
	{
		super("monthReport.ftl", user, pointInMonth, reportedUser);
		timeDAO = new TimeDAO(emf);
	}

	public String getMonth()
	{
		return pointInTime.toString("MMMMMMMMMM");
	}

	public String getDay(int d)
	{
		return pointInTime.withDayOfMonth(d).toString("EEE");
	}

	public int getDaysInMonth()
	{
		return pointInTime.withDayOfMonth(1).plusMonths(1).minusDays(1).getDayOfMonth();
	}

	public List<Entry<String, String>> getTimes(int day)
	{
		DateTime start = pointInTime.withDayOfMonth(day).withTimeAtStartOfDay();
		DateTime stop = start.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		return iGetTimes(start, stop);
	}

	public List<Entry<String, String>> getAllTimes()
	{
		DateTime start = pointInTime.withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime stop = start.plusMonths(1).minusDays(1)
				.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		return iGetTimes(start, stop);
	}

	private List<Entry<String, String>> iGetTimes(DateTime start, DateTime stop)
	{
		PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder().minimumPrintedDigits(TWO).printZeroAlways()
				.appendHours().appendSeparator(":").appendMinutes().toFormatter();

		List<Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();

		List<Entry<Task, Duration>> result = timeDAO.getTimesSummary(user, start, stop);
		for (Entry<Task, Duration> entry : result)
		{
			String name = entry.getKey().getName().toString();
			Period period = entry.getValue().toPeriod();
			String duration = minutesAndSeconds.print(period);

			SimpleEntry<String, String> entry2 = new SimpleEntry<String, String>(name, duration);
			list.add(entry2);
		}
		return list;
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

	public String getNextMonthLink()
	{
		DateTime nextPointInTime = pointInTime.plusMonths(1);
		if (nextPointInTime.isAfterNow())
		{
			return "<button type='button' disabled='disabled'>&gt;&gt;</button>";
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
			return "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		}
		else
		{
			return createUrl(nextPointInTime, true);
		}
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
