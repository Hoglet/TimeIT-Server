package test;

import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.Database;
import se.solit.timeit.DatabaseConfiguration;
import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

public class TestDatabase
{

	private DatabaseConfiguration	dc;

	@Before
	public void setUp() throws Exception
	{
		dc = new DatabaseConfiguration();
		dc.setDriverClass("org.h2.Driver");
		dc.setUrl("jdbc:h2:mem:test2");
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public final void testCreateJpaPersistFactory()
	{
		Database db = new Database(dc);
		EntityManagerFactory emf = db.createJpaPersistFactory();
		RoleDAO roleDAO = new RoleDAO(emf);
		Role role = roleDAO.get(Role.ADMIN);
		Role expected = new Role(Role.ADMIN);
		Assert.assertEquals(expected, role);

		UserDAO userDAO = new UserDAO(emf);
		User admin = userDAO.getUser("admin");
		Assert.assertTrue(admin != null);

		//Get full coverage
		EntityManagerFactory emf2 = db.createJpaPersistFactory();
	}
}
