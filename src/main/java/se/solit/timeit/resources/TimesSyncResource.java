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

import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.entities.Time;

@Path("/sync/times")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimesSyncResource
{
	private final TimeDAO	timeDAO;

	public TimesSyncResource(final EntityManagerFactory emf)
	{
		timeDAO = new TimeDAO(emf);
	}

	@GET
	@Path("/{user}")
	public final Collection<Time> tasksGet(@PathParam("user") final String user) throws SQLException
	{

		return timeDAO.getTimes(user);
	}

	@PUT
	@Path("/{user}")
	public final Collection<Time> tasksSync(@PathParam("user") final String user, final Time[] paramTimes)
			throws SQLException
	{
		timeDAO.updateOrAdd(paramTimes);
		return timeDAO.getTimes(user);
	}
}
