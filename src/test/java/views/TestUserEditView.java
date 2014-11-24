package views;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.UserEditView;

public class TestUserEditView
{
	private final static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static UserEditView					userEditView;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		RoleDAO roleDAO = new RoleDAO(emf);
		Collection<Role> roles = new ArrayList<Role>();
		Role adminRole = new Role(Role.ADMIN);
		Role userRole = new Role("User");
		roleDAO.add(adminRole);
		roleDAO.add(userRole);
		roles.add(userRole);

		User user1 = new User("egon", "Egon Malm", "password2", "email", roles);
		User user2 = new User("test", "Test Tester", "password", "email", roles);
		UserDAO userDAO = new UserDAO(emf);
		userDAO.add(user1);
		userDAO.add(user2);
		userEditView = new UserEditView("egon", emf, user2);
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
	public final void testGetUser()
	{
		Assert.assertEquals(userEditView.getUser().getName(), "Egon Malm");
	}

	@Test
	public final void testGetCurrentUser()
	{
		Assert.assertEquals(userEditView.getCurrentUser().getName(), "Test Tester");
	}

	@Test
	public final void testGetRoles()
	{
		Collection<Role> roles = userEditView.getRoles();
		Assert.assertEquals(roles.size(), 2);
		for (Role role : roles)
		{
			if (role.getName().equals(Role.ADMIN))
			{
				Assert.assertEquals(false, role.getCheckedState());
			}
			else
			{
				Assert.assertEquals(true, role.getCheckedState());
			}
		}

	}

}
