package se.solit.timeit.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

public class Database
{

	private final DatabaseConfiguration	conf;

	public Database(DatabaseConfiguration conf2)
	{
		conf = conf2;
	}

	public EntityManagerFactory createJpaPersistFactory()
	{
		Map<String, String> props = new HashMap<String, String>();
		props.put("javax.persistence.jdbc.url", conf.getUrl());
		props.put("javax.persistence.jdbc.user", conf.getUser());
		props.put("javax.persistence.jdbc.password", conf.getPassword());
		props.put("javax.persistence.jdbc.driver", conf.getDriverClass());
		props.put("javax.persistence.schema-generation.database.action", "create");
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Default", props);
		populateTables(emf);
		cleanData(emf);
		return emf;
	}

	private void cleanData(EntityManagerFactory emf)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("DELETE FROM Time t WHERE t.stop < (t.start +15) ");
		query.executeUpdate();
		em.getTransaction().commit();
	}

	private void populateTables(EntityManagerFactory emf)
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		if (roleDAO.get(Role.ADMIN) == null)
		{
			Role role = new Role(Role.ADMIN);
			roleDAO.add(role);
			Collection<Role> roles = new ArrayList<Role>();
			roles.add(role);
			User user = new User("admin", "", "admin", "", roles);
			UserDAO userDAO = new UserDAO(emf);
			userDAO.add(user);
		}
	}
}
