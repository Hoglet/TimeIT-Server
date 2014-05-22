package se.solit.dwtemplate.resources;

import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.solit.dwtemplate.views.AdminView;


@Path("/admin")
public class AdminResource
{
	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public View admin()
	{
		return new AdminView();
	}

/*	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/user")
	public View userAdmin()
	{
		return new View("/views/useradmin.ftl", Charsets.UTF_8) {};
	}
*/
}
