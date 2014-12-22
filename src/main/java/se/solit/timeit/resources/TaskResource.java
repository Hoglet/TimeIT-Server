package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.TaskAddedView;
import se.solit.timeit.views.TaskView;

@Path("/task")
public class TaskResource
{
	private EntityManagerFactory	emf;

	public TaskResource(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public View getAdd(@Auth User user)
	{
		Task task = new Task(UUID.randomUUID().toString(), "", null, false, 0, false, user);
		return new TaskView(emf, task, user);
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
		Task task = new Task(id, name, parent, false, now.getTime(), false, user);
		taskdao.add(task);
		return new TaskAddedView(user);

	}
}
