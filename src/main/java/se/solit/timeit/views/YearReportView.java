package se.solit.timeit.views;

import io.dropwizard.jersey.sessions.Session;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class YearReportView extends ReportView
{
	private final TimeDAO	timeDAO;

	public YearReportView(EntityManagerFactory emf, DateTime pointInMonth, User user, User reportedUser,
			HttpContext context, @Session HttpSession session)
	{
		super("yearReport.ftl", user, pointInMonth, reportedUser, context, session, emf);
		timeDAO = new TimeDAO(emf);
		DateTime beginingOfYear = pointInTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime endOfYear = beginingOfYear.plusMonths(MONTHS_IN_YEAR).minusDays(1)
				.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		extractTimeDescriptors(beginingOfYear, endOfYear);
		extractTasks();
	}

	public String getMonth(int m)
	{
		DateTime pointInYear = pointInTime.withMonthOfYear(m);
		return pointInYear.toString("MMMMMMMMMM");
	}

	public TimeDescriptorList getTimes(int month)
	{
		DateTime start = pointInTime.withMonthOfYear(month).withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime stop = start.plusMonths(1).minusDays(1)
				.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		return timeDAO.getTimes(user, start, stop);
	}

	public TimeDescriptorList getAllTimes()
	{
		DateTime start = pointInTime.withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		DateTime stop = start.plusMonths(MONTHS_IN_YEAR).minusDays(1)
				.withTime(LAST_HOUR_OF_DAY, LAST_MINUTE_OF_HOUR, LAST_SECOND_OF_MINUTE, 0);
		return timeDAO.getTimes(user, start, stop);
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

	public String getYearLink()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/report/");
		stringBuilder.append(reportedUser.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(pointInTime.getYear()));
		return stringBuilder.toString();
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
