package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class UserAddView extends View
{
	private final User		user;
	private final RoleDAO	roleDAO;

	public UserAddView(EntityManagerFactory emf, User user2)
	{
		super("useradd.ftl", Charsets.UTF_8);
		roleDAO = new RoleDAO(emf);
		user = user2;
	}

	public Collection<Role> getRoles()
	{
		return roleDAO.getRoles();
	}

	public User getCurrentUser()
	{
		return user;
	}
}
