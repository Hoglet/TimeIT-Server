package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.IndexView;

@Path("/")
public class IndexResource
{
	private EntityManagerFactory	emf;

	public IndexResource(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	public View landingPage(@Auth User user)
	{
		return new IndexView(user, emf);
	}
}
