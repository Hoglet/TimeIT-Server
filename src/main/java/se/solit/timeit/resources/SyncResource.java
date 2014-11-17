package se.solit.timeit.resources;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

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

@Path("/sync")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SyncResource
{
	private final EntityManagerFactory	emf;

	public SyncResource(final EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@GET
	@Path("/task/{id}")
	public final Task taskGet(@PathParam("id") final String id)
			throws SQLException
	{
		TaskDAO taskDAO = new TaskDAO(emf);
		return taskDAO.getTask(id);
	}

	@GET
	@Path("/tasks/{user}")
	public final Collection<Task> tasksGet(@PathParam("user") final String user)
			throws SQLException
	{
		TaskDAO taskDAO = new TaskDAO(emf);
		return taskDAO.getTasks(user);
	}

	@PUT
	@Path("/tasks/{user}")
	public final Collection<Task> tasksSync(
			@PathParam("user") final String user, final List<Task> paramTasks)
			throws SQLException
	{
		TaskDAO taskDAO = new TaskDAO(emf);
		return taskDAO.getTasks(user);
	}

}
