package se.solit.timeit.views;

import com.sun.net.httpserver.HttpContext;
import io.dropwizard.jersey.sessions.Session;

import java.time.Instant;
import java.time.LocalDate;
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


public class YearReportView extends ReportView
{
	private final TimeDAO	timeDAO;

	public YearReportView(EntityManagerFactory emf, ZonedDateTime pointInMonth, User user, User reportedUser,
						  UriInfo uriInfo, @Session HttpSession session)
	{
		super("yearReport.ftl", user, pointInMonth, reportedUser, uriInfo, session, emf);
		timeDAO = new TimeDAO(emf);
		ZonedDateTime beginingOfYear = pointInTime.withMonth(1).withDayOfMonth(1).with(LocalDate.MIN);
		ZonedDateTime endOfYear = beginingOfYear.plusMonths(MONTHS_IN_YEAR).minusDays(1).with(LocalDate.MAX);
		extractTimeDescriptors(beginingOfYear, endOfYear);
		extractTasks();
	}

	public String getMonth(int m)
	{
		ZonedDateTime pointInYear = pointInTime.withMonth(m);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
		return pointInYear.format(formatter);
	}

	public TimeDescriptorList getTimes(int month)
	{
		ZonedDateTime start = pointInTime.withMonth(month).withDayOfMonth(1).with(LocalTime.MIN);
		ZonedDateTime stop = start.plusMonths(1).minusDays(1)
				.with(LocalDate.MAX);
		return timeDAO.getTimes(user, start, stop);
	}

	public TimeDescriptorList getAllTimes()
	{
		ZonedDateTime start = pointInTime.withMonth(1).withDayOfMonth(1).with(LocalTime.MIN);
		ZonedDateTime stop = start.plusMonths(MONTHS_IN_YEAR).minusDays(1)
				.with(LocalTime.MAX);
		return timeDAO.getTimes(user, start, stop);
	}

	public String getPreviousYearLink()
	{
		ZonedDateTime nextPointInTime = pointInTime.minusYears(1);
		return createUrl(nextPointInTime, false);
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

	public String getYearLink()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/report/");
		stringBuilder.append(reportedUser.getUsername());
		stringBuilder.append("/");
		stringBuilder.append(String.valueOf(pointInTime.getYear()));
		return stringBuilder.toString();
	}

	private String createUrl(ZonedDateTime nextPointInTime, boolean forward)
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
