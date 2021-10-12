package se.solit.timeit.resources;

import com.sun.net.httpserver.HttpContext;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.sessions.Session;
import io.dropwizard.views.View;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.DayReportView;
import se.solit.timeit.views.MonthReportView;
import se.solit.timeit.views.TaskDetailReportView;
import se.solit.timeit.views.YearReportView;

@Path("/report")
public class ReportResource
{
	private final EntityManagerFactory	emf;
	private final UserDAO				userDAO;

	public ReportResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		userDAO = new UserDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/{username}/{year}")
	@CacheControl(maxAge = 15, maxAgeUnit = TimeUnit.MINUTES)
	public View getYearReport(@Auth User user, @PathParam("username") final String username,
							  @PathParam("year") final int year, @Context UriInfo uriInfo, @Session HttpSession session)
	{
		ZoneId zone = ZonedDateTime.now().getZone();
		YearReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			ZonedDateTime pointInTime = ZonedDateTime.of(year, 1, 1, 1, 1, 0, 0, zone);
			result = new YearReportView(emf, pointInTime, user, userToShow, uriInfo, session);
		}
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		return result;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/{username}/{year}/{month}")
	@CacheControl(maxAge = 15, maxAgeUnit = TimeUnit.MINUTES)
	public View getMonthReport(@Auth User user, @PathParam("username") final String username,
			@PathParam("year") final int year, @PathParam("month") final int month, @Context UriInfo uriInfo,
			@Session HttpSession session)
	{
		ZoneId zone = ZonedDateTime.now().getZone();
		MonthReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			ZonedDateTime pointInTime = ZonedDateTime.of(year, month, 1, 1, 1, 0, 0, zone);
			result = new MonthReportView(emf, pointInTime, user, userToShow, uriInfo, session);
		}
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		return result;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/{username}/{year}/{month}/{day}")
	public View getDayReport(@Auth User user, @PathParam("username") final String username,
			@PathParam("year") final int year, @PathParam("month") final int month, @PathParam("day") final int day,
			@Context UriInfo uriInfo, @Session HttpSession session)
	{
		ZoneId zone = ZonedDateTime.now().getZone();
		DayReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			ZonedDateTime pointInTime = ZonedDateTime.of(year, month, day, 1, 1, 0, 0, zone);
			result = new DayReportView(emf, pointInTime, user, userToShow, uriInfo, session);
		}
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		return result;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/{username}/{year}/{month}/{day}/{task}")
	public View getTaskDetailReport(@Auth User user, @PathParam("username") final String username,
			@PathParam("year") final int year, @PathParam("month") final int month, @PathParam("day") final int day,
			@PathParam("task") final String taskid,
			@Context UriInfo uriInfo, @Session HttpSession session)
	{
		session.setAttribute("returnPoint", uriInfo.getPath());
		TaskDetailReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);
			ZoneId zone = ZonedDateTime.now().getZone();
			ZonedDateTime pointInTime = ZonedDateTime.of(year, month, day, 1, 1, 0, 0, zone);
			result = new TaskDetailReportView(emf, pointInTime, user, userToShow, taskid, uriInfo, session);
		}
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		return result;
	}

}
