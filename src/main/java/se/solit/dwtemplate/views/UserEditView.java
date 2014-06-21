package se.solit.dwtemplate.views;

import io.dropwizard.views.View;

import javax.persistence.EntityManagerFactory;

import se.solit.dwtemplate.User;
import se.solit.dwtemplate.accessors.UserManager;

import com.google.common.base.Charsets;

public class UserEditView extends View
{
	String	username;

	public UserEditView(String username, EntityManagerFactory emf)
	{
		super("useredit.ftl", Charsets.UTF_8);
		userManager = new UserManager(emf);
		this.username = username;
	}

	private final UserManager	userManager;

	public User getUser()
	{
		return userManager.getUser(username);
	}

}
