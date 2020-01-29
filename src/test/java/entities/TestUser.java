package entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.utilities.Crypto;

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
			user = new User(null, "", "abc", "", null);
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
		User newUser = user.withName(JUST_A_STRING);
		assertEquals(JUST_A_STRING, newUser.getName());
		assertEquals(user.getEmail(), newUser.getEmail());
		assertEquals(user.getRoles(), newUser.getRoles());
		assertEquals(user.getUsername(), newUser.getUsername());
		assertEquals(user.getPassword(),newUser.getPassword());
	}

	@Test
	public void testSetEmail()
	{
		User newUser = user.withEmail(JUST_A_STRING);
		assertEquals(JUST_A_STRING.toLowerCase(Locale.ENGLISH), newUser.getEmail());
		assertEquals(user.getName(), newUser.getName());
		assertEquals(user.getRoles(), newUser.getRoles());
		assertEquals(user.getUsername(), newUser.getUsername());
		assertEquals(user.getPassword(), newUser.getPassword());
	}

	@Test
	public void testSetPassword()
	{
		User newUser = user.withPassword(JUST_A_STRING);
		assertEquals(Crypto.encrypt(JUST_A_STRING), newUser.getPassword());
		assertEquals(user.getEmail(), newUser.getEmail());
		assertEquals(user.getName(), newUser.getName());
		assertEquals(user.getRoles(), newUser.getRoles());
		assertEquals(user.getUsername(), newUser.getUsername());
	}

	@Test
	public void testSetRoles()
	{
		Collection<Role> roles = new ArrayList<Role>();
		assertEquals(user.hasRole(Role.ADMIN), false);
		Role adminRole = new Role(Role.ADMIN);
		roles.add(new Role(Role.ADMIN));
		User newUser = user.withRoles(roles);
		Collection<Role> resultingRoles = newUser.getRoles();
		assertEquals(resultingRoles.size(), 1);
		assertEquals(newUser.hasRole(adminRole), true);
		assertEquals(newUser.hasRole(""), false);
		
		assertEquals(user.getEmail(), newUser.getEmail());
		assertEquals(user.getName(), newUser.getName());
		assertEquals(user.getUsername(), newUser.getUsername());
		assertEquals(user.getPassword(), newUser.getPassword());
	}

	@SuppressWarnings("unlikely-arg-type")
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
