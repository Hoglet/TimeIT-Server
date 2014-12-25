package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

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
import se.solit.timeit.views.TaskAddedView;
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
		return new TaskView(emf, task, user, Action.add);
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View postAdd(@Auth User user, @FormParam("name") String name, @FormParam("taskid") String id,
			@FormParam("parent") String parentID)
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
		return new TaskAddedView(user);

	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public View edit(@Auth User user, @QueryParam("taskid") String id)
	{
		TaskDAO taskdao = new TaskDAO(emf);
		Task task = taskdao.getByID(id);
		return new TaskView(emf, task, user, Action.edit);
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
		return new TaskAddedView(user);

	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public View chooser(@Auth User user, @QueryParam("action") String type)
	{
		return new TaskChooserView(emf, user);
	}

}
