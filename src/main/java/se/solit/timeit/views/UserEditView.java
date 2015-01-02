package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

public class UserEditView extends BaseView
{
	private final String	username;
	private final RoleDAO	roleDAO;
	private final UserDAO	userDAO;

	public UserEditView(String username, EntityManagerFactory emf, User user)
	{
		super("useredit.ftl", user);
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
