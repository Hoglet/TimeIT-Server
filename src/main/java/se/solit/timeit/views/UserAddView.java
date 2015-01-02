package se.solit.timeit.views;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

public class UserAddView extends BaseView
{
	private final RoleDAO	roleDAO;

	public UserAddView(EntityManagerFactory emf, User user)
	{
		super("useradd.ftl", user);
		roleDAO = new RoleDAO(emf);
	}

	public Collection<Role> getRoles()
	{
		return roleDAO.getRoles();
	}
}
