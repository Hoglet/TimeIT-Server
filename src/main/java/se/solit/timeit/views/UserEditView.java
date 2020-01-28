package se.solit.timeit.views;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class UserEditView extends BaseView
{
	private final String	username;
	private final RoleDAO	roleDAO;
	private final UserDAO	userDAO;

	public UserEditView(String username, EntityManagerFactory emf, User user, HttpContext context, HttpSession session)
	{
		super("useredit.ftl", user, context, session);
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
		
		Collection<Role> result = new ArrayList<Role>();
		for (Role role : roleDAO.getRoles())
		{
			if (u.hasRole(role))
			{
				role = role.withCheckedState(true);
			}
			result.add(role);
		}
		return result;
	}
}
