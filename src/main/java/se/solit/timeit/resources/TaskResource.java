package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.sessions.Session;
import io.dropwizard.views.View;

import java.net.URISyntaxException;
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
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.Action;
import se.solit.timeit.views.TaskView;

import com.sun.jersey.api.core.HttpContext;

@Path("/task")
public class TaskResource extends BaseResource
{
	private static final String	NOT_ALLOWED	= "Not allowed";
	private final EntityManagerFactory	emf;
	private static final Logger			LOGGER	= LoggerFactory.getLogger(TaskResource.class);

	public TaskResource(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View getAdd(@Auth User user, @Context HttpContext context, @Session HttpSession session)
	{
		Task task = new Task(UUID.randomUUID(), "", null, user);
		return new TaskView(emf, task, user, Action.ADD, context, session);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View postAdd(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID, @Session HttpSession session)
			throws URISyntaxException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task parent = null;
		if (!parentID.isEmpty())
		{
			parent = taskdao.getByID(UUID.fromString(parentID));
		}
		Task task = new Task(UUID.fromString(id), name, parent, user);
		taskdao.add(task);
		String headline = "Task added successfully";
		setMessage(session, headline);
		throw redirect("/");
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/edit/{taskid}")
	public View edit(@Auth User user, @PathParam("taskid") String id, @Context HttpContext context,
			@Session HttpSession session) throws URISyntaxException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		if (task.getOwner().equals(user))
		{
			return new TaskView(emf, task, user, Action.EDIT, context, session);
		}
		setMessage(session, NOT_ALLOWED);
		throw redirect("/");
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View postEdit(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID, @Session HttpSession session)
			throws URISyntaxException
	{
		try
		{
			TaskDAO taskdao = new TaskDAO(emf);
			Task parent = null;
			if (!parentID.isEmpty())
			{
				parent = taskdao.getByID(parentID);
			}
			Task existingTask = taskdao.getByID(id);
			String headline;
			if (existingTask.getOwner().equals(user))
			{
				Task task = new Task(UUID.fromString(id), name, parent, user);
				taskdao.update(task);
				headline = "Task updated";
			}
			else
			{
				headline = NOT_ALLOWED;
			}
			setMessage(session, headline);
		}
		catch (Exception e)
		{
			String errorMessage = "Task edit failed: ";
			setMessage(session, errorMessage + e.getMessage());
			LOGGER.error(errorMessage, e);
		}
		throw redirect("/");
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/delete/{taskid}")
	public View delete(@Auth User user, @PathParam("taskid") String id,
			@Session HttpSession session)
			throws URISyntaxException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		String headline = "Task is deleted";
		if (task.getOwner().equals(user))
		{
			taskdao.delete(task);
		}
		else
		{
			headline = NOT_ALLOWED;
		}
		setMessage(session, headline);
		throw redirect("/");
	}
}
