package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.sessions.Session;
import io.dropwizard.views.View;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.EditTimeView;
import se.solit.timeit.views.TimeView;

import com.sun.jersey.api.core.HttpContext;

@Path("/time")
public class TimeResource extends BaseResource
{
	private final EntityManagerFactory	emf;
	private final TaskDAO				taskDAO;
	private final TimeDAO				timedao;
	private static final Logger			LOGGER	= LoggerFactory.getLogger(TimeResource.class);

	public TimeResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		taskDAO = new TaskDAO(emf);
		timedao = new TimeDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View getAdd(@Auth User user, @Context HttpContext context, @Session HttpSession session)
	{
		Instant now = Instant.now();
		Time time = new Time(UUID.randomUUID(), now, now, false, now, null, "");
		return new TimeView(emf, time, user, context, session);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View postAdd(@Auth User user, @FormParam("timeid") String id, @FormParam("start") String paramStart,
			@FormParam("stop") String paramStop, @FormParam("taskid") String taskID, @FormParam("date") String date,
			@Session HttpSession session)
			throws URISyntaxException
	{
		try
		{
			Instant now = Instant.now();
			ZoneId zone = ZonedDateTime.now().getZone();
			
			LocalDate d = LocalDate.parse(date);
			LocalTime s1 = LocalTime.parse(paramStart);
			LocalTime s2 = LocalTime.parse(paramStop);

			Instant start = ZonedDateTime.of(d, s1, zone).toInstant();
			Instant stop = ZonedDateTime.of(d, s2, zone).toInstant();
			Task task = taskDAO.getByID(taskID);
			Time time = new Time(UUID.fromString(id), start, stop, false, now, task, "");
			timedao.add(time);
			String headline = "Time added successfully";
			setMessage(session, headline);
		}
		catch (Exception e)
		{
			String errorMessage = "Add time failed: ";
			setMessage(session, errorMessage + e.getMessage());
			LOGGER.error(errorMessage, e);

		}
		throw redirect("/");
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/edit/{timeid}")
	public View getEdit(@Auth User user, @PathParam("timeid") String timeid, @Context HttpContext context,
			@Session HttpSession session) throws URISyntaxException
	{
		Time time;
		try
		{
			time = timedao.getByID(UUID.fromString(timeid));
			return new EditTimeView(emf, time, user, context, session);
		}
		catch (SQLException e)
		{
			String message = "Failed to find time item ";
			setMessage(session, message + e.getMessage());
			LOGGER.error(message, e);
			throw redirect("/");
		}
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View postEdit(@Auth User user, @FormParam("timeid") String id, @FormParam("start") String paramStart,
			@FormParam("stop") String paramStop, @FormParam("date") String date,
			@Session HttpSession session)
			throws URISyntaxException
	{
		try
		{
			ZoneId zone = ZonedDateTime.now().getZone();
			LocalDate d = LocalDate.parse(date);
			LocalTime s1 = LocalTime.parse(paramStart);
			LocalTime s2 = LocalTime.parse(paramStop);

			Instant start = ZonedDateTime.of(d, s1, zone).toInstant();
			Instant stop = ZonedDateTime.of(d, s2, zone).toInstant();
			Time time = timedao.getByID(UUID.fromString(id));
			Time newTime = time.withStart(start).withStop(stop);
			timedao.update(newTime);
			String headline = "Time updated successfully";
			setMessage(session, headline);
		}
		catch (Exception e)
		{
			String errorMessage = "Add time failed: ";
			setMessage(session, errorMessage + e.getMessage());
			LOGGER.error(errorMessage, e);

		}
		String returnPath = (String) session.getAttribute("returnPoint");
		throw redirect(returnPath);
	}
}
