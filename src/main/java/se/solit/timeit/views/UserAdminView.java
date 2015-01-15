package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class UserAdminView extends BaseView
{

	private final UserDAO	userManager;

	public UserAdminView(EntityManagerFactory emf, User user)
	{
		super("userAdmin.ftl", user);
		userManager = new UserDAO(emf);
	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}
}
