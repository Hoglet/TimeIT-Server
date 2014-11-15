package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.ws.rs.core.Response.Status;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.AdminView;
import se.solit.timeit.views.UserAddView;
import se.solit.timeit.views.UserEditView;

@Path("/admin")
public class AdminResource
{
	private final EntityManagerFactory	emf;
	private final UserDAO				userManager;

	public AdminResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		userManager = new UserDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public Response admin(@Auth User user)
	{
		if (user.hasRole("Admin"))
		{
			return Response.ok(new AdminView(emf)).build();
		}
		else
		{
			return Response.ok("Access denied").status(Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user/edit")
	public void userEdit(@Auth User authorizedUser, @FormParam("userName") String username,
			@FormParam("name") String name,
			@FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs, @FormParam("submitType") String response)
	{
		if (authorizedUser.hasRole("Admin") && "save".equals(response))
		{
			RoleDAO roleDAO = new RoleDAO(emf);
			User user = userManager.getUser(username);
			user.setName(name);
			user.setEmail(email);
			user.setPassword(password);
			Collection<Role> roles = new ArrayList<Role>();
			for (String id : roleIDs)
			{
				roles.add(roleDAO.get(id));
			}
			user.setRoles(roles);
			userManager.update(user);
		}
		redirect("/admin");
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user/add")
	public void userAdd(@Auth User authorizedUser, @FormParam("userName") String username,
			@FormParam("name") String name,
			@FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs, @FormParam("submitType") String response)
	{
		if (authorizedUser.hasRole("Admin") && "save".equals(response))
		{
			RoleDAO roleDAO = new RoleDAO(emf);
			Collection<Role> roles = new ArrayList<Role>();
			for (String id : roleIDs)
			{
				roles.add(roleDAO.get(id));
			}
			User user = new User(name, username, password, email, roles);
			userManager.add(user);
		}
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
			System.out.println(e);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user")
	public Response user(@Auth User authorizedUser, @FormParam("userSelector") List<String> users,
			@FormParam("submitType") String type)
	{
		Response response = Response.ok("Access denied").status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole("Admin"))
		{
			if ("edit".equals(type))
			{
				response = Response.ok(new UserEditView(users.get(0), emf)).build();
			}
			else if ("add".equals(type))
			{
				response = Response.ok(new UserAddView(emf)).build();
			}
			else if ("OK".equals(type))
			{
				User user = userManager.getUser(users.get(0));
				userManager.delete(user);
				redirect("/admin");
			}
			else
			{
				response = Response.ok(new AdminView(emf)).build();
			}
		}
		return response;
	}
}
