package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class AdminView extends View
{

	private final UserDAO	userManager;
	private final User		user;

	public AdminView(EntityManagerFactory emf, User user2)
	{
		super("admin.ftl");
		userManager = new UserDAO(emf);
		user = user2;
	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}

	public User getCurrentUser()
	{
		return user;
	}
}
