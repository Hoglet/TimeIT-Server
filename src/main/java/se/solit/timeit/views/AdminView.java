package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class AdminView extends BaseView
{

	private final UserDAO	userManager;

	public AdminView(EntityManagerFactory emf, User user)
	{
		super("admin.ftl", user);
		userManager = new UserDAO(emf);
	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}
}
