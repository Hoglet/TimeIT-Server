package entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Role;

public class TestRole
{

	private static final String	JUST_A_STRING	= "Admin";
	private Role				role;

	@Before
	public void setUp() throws Exception
	{
		role = new Role("Admin");
	}

	@Test
	public final void testSetCheckedState()
	{
		assertFalse(role.getCheckedState());
		Role newRole = role.withCheckedState(true);
		assertTrue(newRole.getCheckedState());
		assertEquals(role.getName(), newRole.getName());
	}

	@Test
	public final void testEqualsObject()
	{
		Role x = new Role(JUST_A_STRING);
		Role y = new Role(JUST_A_STRING);

		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		assertFalse(x.equals(""));
		assertFalse(x.equals(null));

	}

}
