package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class UserAdminView extends BaseView
{

	private final UserDAO	userManager;
	private String			message	= "";

	public UserAdminView(EntityManagerFactory emf, User user, HttpContext context, String message)
	{
		super("userAdmin.ftl", user, context);
		userManager = new UserDAO(emf);
		if (message != null)
		{
			this.message = message;
		}

	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}

	public String getMessage()
	{
		return message;
	}
}
