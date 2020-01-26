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

import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

@Path("/sync/times")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimesSyncResource
{
	private static final Logger	LOGGER					= LoggerFactory.getLogger(TimesSyncResource.class);

	private final TimeDAO		timeDAO;

	private final SyncHelper	syncHelper;

	public TimesSyncResource(final EntityManagerFactory emf)
	{
		timeDAO = new TimeDAO(emf);
		syncHelper = new SyncHelper(emf);
	}

	@GET
	@Path("/{user}")
	public final Collection<Time> timesGet(@Auth User authorizedUser, @PathParam("user") final String username)
	{
		syncHelper.verifyHasAccess(authorizedUser, username);
		return timeDAO.getTimes(username);
	}

	@PUT
	@Path("/{user}/{time}")
	public final Collection<Time> timesSync(@Auth User authorizedUser, @PathParam("user") final String username,
			@PathParam("time") final long time,
			final Time[] paramTimes)
	{		
		syncHelper.verifyHasAccess(authorizedUser, username);
		syncHelper.verifyTimesOwnership(authorizedUser, paramTimes);
		Collection<Time> result = null;
		try
		{
			ZoneId zone = ZonedDateTime.now().getZone();
			timeDAO.updateOrAdd(paramTimes);
			result = timeDAO.getTimes(username, Instant.ofEpochSecond(time).atZone(zone));
		}
		catch (SQLException e)
		{
			LOGGER.error("Failed to sync times for user " + authorizedUser.getUsername(), e);
		}
		return result;
	}

	@PUT
	@Path("/{user}")
	public final Collection<Time> timesSync(@Auth User authorizedUser, @PathParam("user") final String username,
			final Time[] paramTimes)
	{
		syncHelper.verifyHasAccess(authorizedUser, username);
		syncHelper.verifyTimesOwnership(authorizedUser, paramTimes);
		Collection<Time> result = null;
		try
		{
			timeDAO.updateOrAdd(paramTimes);
			result = timeDAO.getTimes(username);
		}
		catch (SQLException e)
		{
			LOGGER.error("Failed to sync times for user " + authorizedUser.getUsername(), e);
		}
		return result;
	}

}
