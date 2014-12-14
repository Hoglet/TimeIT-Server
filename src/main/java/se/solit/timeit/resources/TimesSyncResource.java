package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;

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
import se.solit.timeit.entities.User;

@Path("/sync/times")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimesSyncResource
{
	private final TimeDAO	timeDAO;

	private SyncHelper		syncHelper;

	public TimesSyncResource(final EntityManagerFactory emf)
	{
		timeDAO = new TimeDAO(emf);
		syncHelper = new SyncHelper(emf);
	}

	@GET
	@Path("/{user}")
	public final Collection<Time> timesGet(@Auth User authorizedUser, @PathParam("user") final String username)
			throws SQLException
	{
		syncHelper.verifyHasAccess(authorizedUser, username);
		return timeDAO.getTimes(username);
	}

	@PUT
	@Path("/{user}")
	public final Collection<Time> timesSync(@Auth User authorizedUser, @PathParam("user") final String username,
			final Time[] paramTimes) throws SQLException
	{
		syncHelper.verifyHasAccess(authorizedUser, username);
		syncHelper.verifyTimesOwnership(authorizedUser, paramTimes);
		timeDAO.updateOrAdd(paramTimes);
		return timeDAO.getTimes(username);
	}
}
