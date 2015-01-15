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

import com.sun.jersey.api.core.HttpContext;

public class YearReportView extends ReportView
{
	private final TimeDAO	timeDAO;

	public YearReportView(EntityManagerFactory emf, DateTime pointInMonth, User user, User reportedUser,
			HttpContext context)
	{
		super("yearReport.ftl", user, pointInMonth, reportedUser, context);
		timeDAO = new TimeDAO(emf);
	}

	public String getMonth(int m)
	{
		DateTime pointInYear = pointInTime.withMonthOfYear(m);
		return pointInYear.toString("MMMMMMMMMM");
	}

	public List<Entry<String, String>> getTimes(int month)
	{
		DateTime start = pointInTime.withMonthOfYear(month).withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime stop = start.plusMonths(1).minusDays(1)
				.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		return iGetTimes(start, stop);
	}

	public List<Entry<String, String>> getAllTimes()
	{
		DateTime start = pointInTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime stop = start.plusMonths(MONTHS_IN_YEAR).minusDays(1)
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

	public String getPreviousYearLink()
	{
		DateTime nextPointInTime = pointInTime.minusYears(1);
		return createUrl(nextPointInTime, false);
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
