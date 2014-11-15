package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class AdminView extends View
{

	private final UserDAO	userManager;

	public AdminView(EntityManagerFactory emf)
	{
		super("admin.ftl");
		userManager = new UserDAO(emf);
	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}

}
