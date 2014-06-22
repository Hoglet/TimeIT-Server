package se.solit.dwtemplate.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.dwtemplate.dao.RoleDAO;
import se.solit.dwtemplate.dao.UserDAO;
import se.solit.dwteplate.entities.Role;
import se.solit.dwteplate.entities.User;

import com.google.common.base.Charsets;

public class UserEditView extends View
{
	private final String	username;
	private final RoleDAO	roleDAO;
	private final UserDAO	userDAO;

	public UserEditView(String username, EntityManagerFactory emf)
	{
		super("useredit.ftl", Charsets.UTF_8);
		userDAO = new UserDAO(emf);
		roleDAO = new RoleDAO(emf);
		this.username = username;
	}

	public User getUser()
	{
		return userDAO.getUser(username);
	}

	public Collection<Role> getRoles()
	{
		User user = userDAO.getUser(username);
		Collection<Role> roles = roleDAO.getRoles();

		for (Role role : roles)
		{
			for (Role userRole : user.getRoles())
			{
				if (role.getName().equals(userRole.getName()))
				{
					role.setCheckedState(true);
				}
			}

		}
		return roles;
	}
}
