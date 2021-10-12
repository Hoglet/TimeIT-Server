package se.solit.timeit.views;

import com.sun.net.httpserver.HttpContext;
import io.dropwizard.jersey.sessions.Session;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.User;

public class MonthReportView extends ReportView
{
	private final TimeDAO       timeDAO;
	private final ZonedDateTime beginingOfMonth;
	private final ZonedDateTime endOfMonth;

	public MonthReportView(EntityManagerFactory emf, ZonedDateTime pointInMonth, User user, User reportedUser,
						   UriInfo uriInfo, @Session HttpSession session)
	{
		super("monthReport.ftl", user, pointInMonth, reportedUser, uriInfo, session, emf);
		timeDAO = new TimeDAO(emf);
		beginingOfMonth = pointInTime.withDayOfMonth(1).with(LocalTime.MIN);
		endOfMonth = beginingOfMonth.plusMonths(1).minusDays(1)
				.with(LocalTime.MAX);
		extractTimeDescriptors(beginingOfMonth, endOfMonth);
		extractTasks();
	}

	public String getMonth()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
		return pointInTime.format(formatter);
	}

	public String getDay(int d)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
		return pointInTime.withDayOfMonth(d).format(formatter);
	}

	public int getDaysInMonth()
	{
		return pointInTime.withDayOfMonth(1).plusMonths(1).minusDays(1).getDayOfMonth();
	}

	public TimeDescriptorList getTimes(int day)
	{
		ZonedDateTime start = pointInTime.withDayOfMonth(day).with(LocalTime.MIN);
		ZonedDateTime stop = start.with(LocalTime.MAX);
		return timeDAO.getTimes(user, start, stop);
	}

	public TimeDescriptorList getAllTimes()
	{
		return times;
	}

	public String getPreviousMonthLink()
	{
		ZonedDateTime nextPointInTime = pointInTime.minusMonths(1);
		return createUrl(nextPointInTime, false);
	}

	public String getPreviousYearLink()
	{
		ZonedDateTime nextPointInTime = pointInTime.minusYears(1);
		return createUrl(nextPointInTime, false);
	}

	public String getNextMonthLink()
	{
		Instant nextPointInTime = Instant.from(pointInTime.plusMonths(1));
		if (nextPointInTime.isAfter(Instant.now()))
		{
			return "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		}
		else
		{
			ZoneId zone = ZonedDateTime.now().getZone();
			return createUrl( nextPointInTime.atZone(zone), true);
		}
	}

	public String getNextYearLink()
	{
		Instant nextPointInTime = Instant.from(pointInTime.plusYears(1));
		if (nextPointInTime.isAfter(Instant.now()))
		{
			return "<button type='button' disabled='disabled'>&gt;&gt;</button>";
		}
		else
		{
			ZoneId zone = ZonedDateTime.now().getZone();
			return createUrl(nextPointInTime.atZone(zone), true);
		}
	}

	public String getMonthLink()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/report/");
		stringBuilder.append(reportedUser.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(pointInTime.getYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(pointInTime.getMonthValue()));
		return stringBuilder.toString();
	}

	private String createUrl(ZonedDateTime nextPointInTime, boolean forward)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<a href='/report/");
		stringBuilder.append(reportedUser.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(nextPointInTime.getYear()));
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(nextPointInTime.getMonthValue()));
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
