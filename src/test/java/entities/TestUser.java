package entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;

public class TestUser
{

	private static final String	JUST_A_STRING	= "Apa";
	private User				user;

	@Before
	public void setUp()
	{
		user = new User();
	}

	@Test
	public final void forceUsername()
	{
		try
		{
			user = new User(null, "", "", "", null);
			Assert.fail("Should not allow null user");
		}
		catch (Exception e)
		{
			Assert.assertEquals(NullPointerException.class, e.getClass());
		}
	}

	@Test
	public void testSetName()
	{
		user.setName(JUST_A_STRING);
		assertEquals(user.getName(), JUST_A_STRING);
	}

	@Test
	public void testSetEmail()
	{
		user.setEmail(JUST_A_STRING);
		assertEquals(user.getEmail(), JUST_A_STRING);
	}

	@Test
	public void testSetPassword()
	{
		user.setPassword(JUST_A_STRING);
		assertEquals(user.getPassword(), JUST_A_STRING);
	}

	@Test
	public void testSetRoles()
	{
		Collection<Role> roles = new ArrayList<Role>();
		assertEquals(user.hasRole(Role.ADMIN), false);
		Role adminRole = new Role(Role.ADMIN);
		roles.add(new Role(Role.ADMIN));
		user.setRoles(roles);
		Collection<Role> resultingRoles = user.getRoles();
		assertEquals(resultingRoles.size(), 1);
		assertEquals(user.hasRole(adminRole), true);
		assertEquals(user.hasRole(""), false);
	}

	@Test
	public void testEquals_Symmetric()
	{
		Collection<Role> roles = new ArrayList<Role>();
		Collection<Role> roles2 = new ArrayList<Role>();

		User x = new User("testman", "Test Tester", "Password", "email", roles); // equals
																					// and
																					// hashCode
																					// check
																					// name
																					// field
																					// value
		User y = new User("testman", "Test Tester", "Password", "email", roles);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new User("agda", "Test Tester", "Password", "email", roles);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new User("testman", null, "Password", "email", roles);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new User("testman", "Test Tester", null, "email", roles);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new User("testman", "Test Tester", "Password", null, roles);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new User("testman", "Test Tester", "Password", "email", null);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		roles2.add(new Role("Apa"));
		y = new User("testman", "Test Tester", "Password", "email", roles2);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new User("testman", null, "Password", "email", roles);
		y = new User("testman", null, "Password", "email", roles);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new User("testman", "Test Tester", null, "email", roles);
		y = new User("testman", "Test Tester", null, "email", roles);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new User("testman", "Test Tester", "Password", null, roles);
		y = new User("testman", "Test Tester", "Password", null, roles);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new User("testman", "Test Tester", "Password", null, null);
		y = new User("testman", "Test Tester", "Password", null, null);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		assertTrue(x.equals(x));
		assertFalse(x.equals(null));
		assertFalse(x.equals("Tjohopp"));

	}
}
