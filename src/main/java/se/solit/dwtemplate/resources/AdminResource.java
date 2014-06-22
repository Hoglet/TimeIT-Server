package se.solit.dwtemplate.resources;

import io.dropwizard.views.View;

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

import se.solit.dwtemplate.dao.RoleDAO;
import se.solit.dwtemplate.dao.UserDAO;
import se.solit.dwtemplate.views.AdminView;
import se.solit.dwtemplate.views.UserAddView;
import se.solit.dwtemplate.views.UserEditView;
import se.solit.dwteplate.entities.Role;
import se.solit.dwteplate.entities.User;

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
	public View admin()
	{
		return new AdminView(emf);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user/edit")
	public void userEdit(@FormParam("userName") String username, @FormParam("name") String name,
			@FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs)
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
		redirect("/admin");
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("text/html;charset=UTF-8")
	@Path("/user/add")
	public void userAdd(@FormParam("userName") String username, @FormParam("name") String name,
			@FormParam("password") String password, @FormParam("email") String email,
			@FormParam("roles") List<String> roleIDs)
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		Collection<Role> roles = new ArrayList<Role>();
		for (String id : roleIDs)
		{
			roles.add(roleDAO.get(id));
		}
		User user = new User(name, username, password, email, roles);
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
			view = new UserEditView(users.get(0), emf);
		}
		else if (type.equals("add"))
		{
			view = new UserAddView(emf);
		}
		else if (type.equals("OK"))
		{
			User user = userManager.getUser(users.get(0));
			userManager.delete(user);
			redirect("/admin");
		}
		else
		{
			view = new AdminView(emf);
		}
		return view;
	}

}
