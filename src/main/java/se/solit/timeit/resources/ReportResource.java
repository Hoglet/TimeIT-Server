package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import javax.persistence.EntityManagerFactory;
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
import se.solit.timeit.views.MonthReportView;
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
			@PathParam("year") final String year, @Context HttpContext context)
	{
		YearReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			DateTime pointInTime = new DateTime(Integer.parseInt(year), 1, 1, 1, 1);
			result = new YearReportView(emf, pointInTime, user, userToShow, context);
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
			@PathParam("year") final String year, @PathParam("month") final String month, @Context HttpContext context)
	{
		MonthReportView result = null;
		if (user.getUsername().equals(username))
		{
			User userToShow = userDAO.getUser(username);

			DateTime pointInTime = new DateTime(Integer.parseInt(year), Integer.parseInt(month), 1, 1, 1);
			result = new MonthReportView(emf, pointInTime, user, userToShow, context);
		}
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		return result;
	}
}
