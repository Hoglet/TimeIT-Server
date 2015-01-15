package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class UserAddView extends BaseView
{
	private final RoleDAO	roleDAO;

	public UserAddView(EntityManagerFactory emf, User user, HttpContext context)
	{
		super("useradd.ftl", user, context);
		roleDAO = new RoleDAO(emf);
	}

	public Collection<Role> getRoles()
	{
		return roleDAO.getRoles();
	}
}
