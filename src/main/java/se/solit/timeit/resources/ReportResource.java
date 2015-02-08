package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.sessions.Session;
import io.dropwizard.views.View;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.DayReportView;
import se.solit.timeit.views.MonthReportView;
import se.solit.timeit.views.TaskDetailReportView;
import se.solit.timeit.views.YearReportView;

import com.sun.jersey.api.core.HttpContext;

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
	public View getYearReport(@Auth User user, @PathParam("username") final String username,
			@PathParam("year") final int year, @Context HttpContext context, @Session HttpSession session)
	{
		YearReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			DateTime pointInTime = new DateTime(year, 1, 1, 1, 1);
			result = new YearReportView(emf, pointInTime, user, userToShow, context, session);
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
	public View getMonthReport(@Auth User user, @PathParam("username") final String username,
			@PathParam("year") final int year, @PathParam("month") final int month, @Context HttpContext context,
			@Session HttpSession session)
	{
		MonthReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			DateTime pointInTime = new DateTime(year, month, 1, 1, 1);
			result = new MonthReportView(emf, pointInTime, user, userToShow, context, session);
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
			@Context HttpContext context, @Session HttpSession session)
	{
		DayReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			DateTime pointInTime = new DateTime(year, month, day, 1, 1);
			result = new DayReportView(emf, pointInTime, user, userToShow, context, session);
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
			@Context HttpContext context, @Session HttpSession session)
	{
		session.setAttribute("returnPoint", context.getRequest().getPath());
		TaskDetailReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			DateTime pointInTime = new DateTime(year, month, day, 1, 1);
			result = new TaskDetailReportView(emf, pointInTime, user, userToShow, taskid, context, session);
		}
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		return result;
	}

}
