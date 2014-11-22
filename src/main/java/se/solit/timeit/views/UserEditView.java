package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class UserEditView extends View
{
	private final String	username;
	private final RoleDAO	roleDAO;
	private final UserDAO	userDAO;
	private final User		user;

	public UserEditView(String username, EntityManagerFactory emf, User currentUser)
	{
		super("useredit.ftl", Charsets.UTF_8);
		userDAO = new UserDAO(emf);
		roleDAO = new RoleDAO(emf);
		this.username = username;
		user = currentUser;
	}

	public User getUser()
	{
		return userDAO.getUser(username);
	}

	public User getCurrentUser()
	{
		return user;
	}

	public Collection<Role> getRoles()
	{
		User u = userDAO.getUser(username);
		Collection<Role> roles = roleDAO.getRoles();

		for (Role role : roles)
		{
			if (u.hasRole(role))
			{
				role.setCheckedState(true);
			}

		}
		return roles;
	}
}
