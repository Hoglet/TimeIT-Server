package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.UserAddView;

public class TestUserAddView
{

	private static EntityManagerFactory	emf;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		emf = Persistence.createEntityManagerFactory("test");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		emf.close();
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public final void testGetRoles()
	{
		Role adminRole = new Role(Role.ADMIN);
		Role otherRole = new Role("Other");
		RoleDAO roleDAO = new RoleDAO(emf);
		roleDAO.add(otherRole);
		roleDAO.add(adminRole);
		User user2 = new User("minion", "Do Er", "password", "email", null);
		UserAddView userAddView = new UserAddView(emf, user2);
		Assert.assertEquals(2, userAddView.getRoles().size());
	}

	@Test
	public final void testGetCurrentUser()
	{
		User user2 = new User("minion", "Do Er", "password", "email", null);
		UserAddView userAddView = new UserAddView(emf, user2);
		Assert.assertEquals(user2, userAddView.getCurrentUser());
	}
}
