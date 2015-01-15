package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.joda.time.DateTime;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.Action;
import se.solit.timeit.views.MessageView;
import se.solit.timeit.views.TaskChooserView;
import se.solit.timeit.views.TaskView;

import com.sun.jersey.api.core.HttpContext;

@Path("/task")
public class TaskResource
{
	private final EntityManagerFactory	emf;

	public TaskResource(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View getAdd(@Auth User user, @Context HttpContext context)
	{
		Task task = new Task(UUID.randomUUID(), "", null, false, DateTime.now(), false, user);
		return new TaskView(emf, task, user, Action.ADD, context);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View postAdd(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID, @Context HttpContext context) throws MalformedURLException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task parent = null;
		if (!parentID.isEmpty())
		{
			parent = taskdao.getByID(UUID.fromString(parentID));
		}
		DateTime now = DateTime.now();
		Task task = new Task(UUID.fromString(id), name, parent, false, now, false, user);
		taskdao.add(task);
		String headline = "Task added successfully";
		String url = "/";
		return new MessageView(user, headline, "", url, context);

	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View edit(@Auth User user, @QueryParam("taskid") String id, @Context HttpContext context)
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		return new TaskView(emf, task, user, Action.EDIT, context);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View postEdit(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID, @Context HttpContext context) throws SQLException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task parent = null;
		if (!parentID.isEmpty())
		{
			parent = taskdao.getByID(parentID);
		}
		DateTime now = DateTime.now();
		Task task = new Task(UUID.fromString(id), name, parent, false, now, false, user);
		taskdao.update(task);
		String headline = "Task updated";
		String text = "";
		String url = "/";
		return new MessageView(user, headline, text, url, context);

	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public View chooser(@Auth User user, @QueryParam("action") String typeString, @Context HttpContext context)
	{
		Action type = Action.ADD;
		if ("delete".equals(typeString))
		{
			type = Action.DELETE;
		}
		if ("edit".equals(typeString))
		{
			type = Action.EDIT;
		}
		return new TaskChooserView(emf, user, type, context);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/delete")
	public View delete(@Auth User user, @FormParam("taskid") String id, @Context HttpContext context)
			throws SQLException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		taskdao.delete(task);
		String headline = "Task is deleted";
		String url = "/";
		String text = "";
		return new MessageView(user, headline, text, url, context);
	}
}
