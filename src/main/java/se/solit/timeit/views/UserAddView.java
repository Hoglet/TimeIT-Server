package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;

import com.google.common.base.Charsets;

public class UserAddView extends View
{
	private final EntityManagerFactory	emf;

	public UserAddView(EntityManagerFactory emf)
	{
		super("useradd.ftl", Charsets.UTF_8);
		this.emf = emf;
	}

	public Collection<Role> getRoles()
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		return roleDAO.getRoles();
	}
}
