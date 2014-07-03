package se.solit.dwtemplate.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.solit.dwtemplate.views.IndexView;
import se.solit.dwteplate.entities.User;

@Path("/")
public class IndexResource
{
	@GET
	@Produces("text/html;charset=UTF-8")
	public View landingPage(@Auth User user)
	{
		return new IndexView();
	}
}
