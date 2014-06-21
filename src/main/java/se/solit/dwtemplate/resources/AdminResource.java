package se.solit.dwtemplate.resources;

import io.dropwizard.views.View;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import se.solit.dwtemplate.User;
import se.solit.dwtemplate.accessors.UserManager;
import se.solit.dwtemplate.views.AdminView;
import se.solit.dwtemplate.views.UserAddView;
import se.solit.dwtemplate.views.UserEditView;

@Path("/admin")
public class AdminResource
{
	private final EntityManagerFactory	emf;
	private final UserManager			userManager;

	public AdminResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		userManager = new UserManager(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public View admin()
	{
		return new AdminView(emf);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user/edit")
	public void userEdit(@FormParam("userSelector") List<String> users, @FormParam("submitType") String type)
	{
		redirect("/admin");
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user/add")
	public void userAdd(@FormParam("userName") String username, @FormParam("name") String name,
			@FormParam("password") String password, @FormParam("email") String email)
	{
		User user = new User(name, username, password, email);
		userManager.add(user);
		redirect("/admin");
	}

	private void redirect(String destination)
	{
		try
		{
			URI uri = new URI(destination);
			Response response = Response.seeOther(uri).build();
			throw new WebApplicationException(response);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user")
	public View user(@FormParam("userSelector") List<String> users, @FormParam("submitType") String type)
	{
		View view = null;
		if (type.equals("edit"))
		{
			view = new UserEditView();
		}
		else if (type.equals("add"))
		{
			view = new UserAddView();
		}
		else
		{
			view = new AdminView(emf);
		}
		return view;
	}

}
