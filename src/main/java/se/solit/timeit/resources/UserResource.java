package se.solit.timeit.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.sessions.Session;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mockito.Mockito;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.DeleteUserView;
import se.solit.timeit.views.UserAddView;
import se.solit.timeit.views.UserAdminView;
import se.solit.timeit.views.UserEditView;

import com.sun.jersey.api.core.HttpContext;

@Path("/user")
public class UserResource extends BaseResource
{
	private static final String			ACCESS_DENIED	= "Access denied";
	private static final String			ADMIN_PATH		= "/user/";
	private final EntityManagerFactory	emf;
	private final UserDAO				userManager;
	private final HttpSession			session			= Mockito.mock(HttpSession.class);

	public UserResource(EntityManagerFactory emf)
	{
		this.emf = emf;
		userManager = new UserDAO(emf);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/")
	public Response admin(@Auth User user, @Context HttpContext context, @Session HttpSession session)
	{
		if (user.hasRole(Role.ADMIN))
		{
			return Response.ok(new UserAdminView(emf, user, context, session)).build();
		}
		else
		{
			return Response.ok("Access denied").status(Status.UNAUTHORIZED).build();
		}
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/{username}")
	public Response userEdit(@Auth User authorizedUser, @PathParam("username") final String username,
			@Context HttpContext context)
			throws URISyntaxException
	{
		Response response = Response.ok(ACCESS_DENIED).status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole(Role.ADMIN) || authorizedUser.getUsername().equals(username))
		{
			response = Response.ok(new UserEditView(username, emf, authorizedUser, context, session)).build();
		}
		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/{username}")
	public void userEdit(@Auth User authorizedUser, @PathParam("username") String username,
			@FormParam("name") String name, @FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs) throws URISyntaxException
	{
		if (authorizedUser.hasRole(Role.ADMIN) || authorizedUser.getUsername().equals(username))
		{
			User user = assignUserValues(authorizedUser, username, name, password, email, roleIDs);
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

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/add")
	public Response userAdd(@Auth User authorizedUser, @Context HttpContext context, @Session HttpSession session)
			throws URISyntaxException
	{
		Response response = Response.ok(ACCESS_DENIED).status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			response = Response.ok(new UserAddView(emf, authorizedUser, context, session)).build();
		}
		return response;
	}

	private User assignUserValues(User authorizedUser, String username, String name, String password, String email,
			List<String> roleIDs)
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		User user = userManager.getUser(username);
		User newUser = user.withName(name).withEmail(email);
		if (password.length() > 0)
		{
			newUser = newUser.withPassword(password);
		}
		Collection<Role> roles = new ArrayList<Role>();
		for (String id : roleIDs)
		{
			roles.add(roleDAO.get(id));
		}
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			newUser = newUser.withRoles(roles);
		}
		return newUser;
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
		else
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
		throw redirect(ADMIN_PATH);
	}

	@GET
	@Produces("text/html;charset=UTF-8")
	@Path("/delete/{username}")
	public Response deleteConfirm(@Auth User authorizedUser, @PathParam("username") final String username,
			@Context HttpContext context, @Session HttpSession session)
	{
		Response response = Response.ok(ACCESS_DENIED).status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			response = Response.ok(new DeleteUserView(emf, authorizedUser, username, context, session)).build();
		}
		return response;
	}

	@POST
	@Produces("text/html;charset=UTF-8")
	@Path("/delete/{username}")
	public Response delete(@Auth User authorizedUser, @PathParam("username") final String username,
			@Session HttpSession session) throws URISyntaxException
	{
		Response response = Response.ok(ACCESS_DENIED).status(Status.UNAUTHORIZED).build();
		if (authorizedUser.hasRole(Role.ADMIN))
		{
			User user = userManager.getUser(username);
			userManager.delete(user);
			String message = "Deleted user " + username;
			session.setAttribute("message", message);
			throw redirect(ADMIN_PATH);
		}
		return response;
	}

}
