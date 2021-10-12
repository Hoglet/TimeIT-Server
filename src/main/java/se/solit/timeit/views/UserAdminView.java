package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class UserAdminView extends BaseView
{

	private final UserDAO	userManager;

	public UserAdminView(EntityManagerFactory emf, User user, UriInfo uriInfo, HttpSession session)
	{
		super("userAdmin.ftl", user, uriInfo, session);
		userManager = new UserDAO(emf);

	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}

}
