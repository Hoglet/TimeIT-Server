package se.solit.timeit.resources;

import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;

@Path("/sync/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TasksSyncResource
{
	private final TaskDAO	taskDAO;

	public TasksSyncResource(final EntityManagerFactory emf)
	{
		taskDAO = new TaskDAO(emf);
	}

	@GET
	@Path("/{user}")
	public final Collection<Task> tasksGet(@PathParam("user") final String user) throws SQLException
	{
		return taskDAO.getTasks(user);
	}

	@PUT
	@Path("/{user}")
	public final Collection<Task> tasksSync(@PathParam("user") final String user, final Task[] paramTasks)
			throws SQLException
	{
		taskDAO.updateOrAdd(paramTasks);
		return taskDAO.getTasks(user);
	}
}
