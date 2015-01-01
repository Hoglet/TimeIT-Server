package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import java.sql.SQLException;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.Action;
import se.solit.timeit.views.MessageView;
import se.solit.timeit.views.TimeView;

@Path("/time")
public class TimeResource
{
	private final EntityManagerFactory	emf;
	private final TaskDAO				taskDAO;
	private final TimeDAO				timedao;

	public TimeResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		taskDAO = new TaskDAO(emf);
		timedao = new TimeDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View getAdd(@Auth User user)
	{
		DateTime now = DateTime.now();
		Time time = new Time(UUID.randomUUID().toString(), now, now, false, now, null);
		return new TimeView(emf, time, user, Action.ADD);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View postAdd(@Auth User user, @FormParam("timeid") String id, @FormParam("start") String paramStart,
			@FormParam("stop") String paramStop, @FormParam("taskid") String taskID, @FormParam("date") String date)
			throws SQLException
	{
		DateTime now = DateTime.now();

		LocalDate d = LocalDate.parse(date);
		LocalTime s1 = LocalTime.parse(paramStart);
		LocalTime s2 = LocalTime.parse(paramStop);

		DateTime start = d.toDateTime(s1);
		DateTime stop = d.toDateTime(s2);
		Task task = taskDAO.getByID(taskID);
		Time time = new Time(id, start, stop, false, now, task);
		timedao.add(time);
		String headline = "Time added successfully";
		String url = "/";
		return new MessageView(user, headline, "", url);

	}
}
