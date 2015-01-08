package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.AdminView;

@Path("/admin")
public class AdminResource
{
	private final EntityManagerFactory	emf;

	public AdminResource(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public Response admin(@Auth User user)
	{
		if (user.hasRole(Role.ADMIN))
		{
			return Response.ok(new AdminView(emf, user)).build();
		}
		else
		{
			return Response.ok("Access denied").status(Status.UNAUTHORIZED).build();
		}
	}

}
