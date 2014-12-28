package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.Action;
import se.solit.timeit.views.MessageView;
import se.solit.timeit.views.TaskChooserView;
import se.solit.timeit.views.TaskView;

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
	public View getAdd(@Auth User user)
	{
		Task task = new Task(UUID.randomUUID().toString(), "", null, false, new Date(), false, user);
		return new TaskView(emf, task, user, Action.ADD);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View postAdd(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID) throws MalformedURLException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task parent = null;
		if (!parentID.isEmpty())
		{
			parent = taskdao.getByID(parentID);
		}
		Date now = new Date();
		Task task = new Task(id, name, parent, false, now, false, user);
		taskdao.add(task);
		String headline = "Task added successfully";
		String url = "/";
		return new MessageView(user, headline, "", url);

	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View edit(@Auth User user, @QueryParam("taskid") String id)
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		return new TaskView(emf, task, user, Action.EDIT);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View postEdit(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID) throws SQLException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task parent = null;
		if (!parentID.isEmpty())
		{
			parent = taskdao.getByID(parentID);
		}
		Date now = new Date();
		Task task = new Task(id, name, parent, false, now, false, user);
		taskdao.update(task);
		String headline = "Task updated";
		String text = "";
		String url = "/";
		return new MessageView(user, headline, text, url);

	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public View chooser(@Auth User user, @QueryParam("action") String typeString)
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
		return new TaskChooserView(emf, user, type);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/delete")
	public View delete(@Auth User user, @FormParam("taskid") String id) throws SQLException
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		taskdao.delete(task);
		String headline = "Task is deleted";
		String url = "/";
		String text = "";
		return new MessageView(user, headline, text, url);
	}
}
