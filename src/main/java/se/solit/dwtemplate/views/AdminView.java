package se.solit.dwtemplate.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.dwtemplate.User;
import se.solit.dwtemplate.accessors.UserManager;

public class AdminView extends View
{

	private final UserManager	userManager;

	public AdminView(EntityManagerFactory emf)
	{
		super("admin.ftl");
		userManager = new UserManager(emf);
	}

	public Collection<User> getUsers()
	{
		return userManager.getUsers();
	}

}
