package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

@Path("/sync/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TasksSyncResource
{
	private static final Logger	LOGGER     = LoggerFactory.getLogger(TasksSyncResource.class);
	private final TaskDAO		taskDAO;
	private final SyncHelper	syncHelper;

	public TasksSyncResource(final EntityManagerFactory emf)
	{
		taskDAO = new TaskDAO(emf);
		syncHelper = new SyncHelper(emf);
	}

	@GET
	@Path("/{username}")
	public final Collection<Task> tasksGet(@Auth User authorizedUser, @PathParam("username") final String username)
	{
		syncHelper.verifyHasAccess(authorizedUser, username);
		return taskDAO.getAllTasks(username);
	}

	@PUT
	@Path("/{user}")
	public final Collection<Task> tasksSync(@Auth User authorizedUser, @PathParam("user") final String username,
			final Task[] paramTasks)
	{
		syncHelper.verifyHasAccess(authorizedUser, username);
		syncHelper.verifyTaskOwnership(authorizedUser, paramTasks);
		try
		{
			taskDAO.updateOrAdd(paramTasks);
		}
		catch (SQLException e)
		{
			LOGGER.error("Error syncing tasks for user " + authorizedUser.getUsername(), e);
		}
		return taskDAO.getAllTasks(username);
	}

	@PUT
	@Path("/{user}/{time}")
	public final Collection<Task> tasksSync(@Auth User authorizedUser, @PathParam("user") final String username,
			@PathParam("time") final long time,
			final Task[] paramTasks)
	{
		ZoneId zone = ZonedDateTime.now().getZone();
		syncHelper.verifyHasAccess(authorizedUser, username);
		syncHelper.verifyTaskOwnership(authorizedUser, paramTasks);
		try
		{
			taskDAO.updateOrAdd(paramTasks);
		}
		catch (SQLException e)
		{
			LOGGER.error("Error syncing tasks for user " + authorizedUser.getUsername(), e);
		}
		return taskDAO.getAllTasks(username, Instant.ofEpochSecond(time).atZone(zone));
	}

}
