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
import javax.ws.rs.PathParam;
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

@Path("/user")
public class UserResource
{
	private static final String			ADMIN_PATH	= "/admin";
	private final EntityManagerFactory	emf;
	private final UserDAO				userManager;

	public UserResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		userManager = new UserDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/edit/{username}")
	public Response userEdit(@Auth User authorizedUser, @PathParam("username") final String username)
			throws URISyntaxException
	{
		Response response = Response.ok("Access denied").status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole(Role.ADMIN) || authorizedUser.getUsername().equals(username))
		{
			response = Response.ok(new UserEditView(username, emf, authorizedUser)).build();
		}
		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/edit")
	public void userEdit(@Auth User authorizedUser, @FormParam("userName") String username,
			@FormParam("name") String name, @FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs) throws URISyntaxException
	{
		if (authorizedUser.hasRole(Role.ADMIN) || authorizedUser.getUsername().equals(username))
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
			if (authorizedUser.hasRole(Role.ADMIN))
			{
				user.setRoles(roles);
			}
			userManager.update(user);
		}
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			throw redirect(ADMIN_PATH);
		}
		else
		{
			throw redirect("/");
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public void userAdd(@Auth User authorizedUser, @FormParam("userName") String username,
			@FormParam("name") String name, @FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs) throws URISyntaxException
	{
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			RoleDAO roleDAO = new RoleDAO(emf);
			Collection<Role> roles = new ArrayList<Role>();
			for (String id : roleIDs)
			{
				roles.add(roleDAO.get(id));
			}
			User user = new User(username, name, password, email, roles);
			userManager.add(user);
		}
		throw redirect(ADMIN_PATH);
	}

	private WebApplicationException redirect(String destination) throws URISyntaxException
	{
		URI uri = new URI(destination);
		Response response = Response.seeOther(uri).build();
		return new WebApplicationException(response);

	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public Response user(@Auth User authorizedUser, @FormParam("userSelector") List<String> users,
			@FormParam("submitType") String type) throws URISyntaxException
	{
		Response response = Response.ok("Access denied").status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			if ("edit".equals(type))
			{
				response = Response.ok(new UserEditView(users.get(0), emf, authorizedUser)).build();
			}
			else if ("add".equals(type))
			{
				response = Response.ok(new UserAddView(emf, authorizedUser)).build();
			}
			else if ("OK".equals(type))
			{
				User user = userManager.getUser(users.get(0));
				userManager.delete(user);
				response = Response.ok(new AdminView(emf, authorizedUser)).build();
			}
			else
			{
				response = Response.ok(new AdminView(emf, authorizedUser)).build();
			}
		}
		return response;
	}
}
