package se.solit.timeit.views;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class DeleteUserView extends BaseView
{
	private final User	user2Delete;

	public DeleteUserView(EntityManagerFactory emf, User authorizedUser, String username, HttpContext context)
	{
		super("userDelete.ftl", authorizedUser, context);
		UserDAO userDAO = new UserDAO(emf);
		user2Delete = userDAO.getUser(username);
	}

	public User getUser()
	{
		return user2Delete;
	}

}
