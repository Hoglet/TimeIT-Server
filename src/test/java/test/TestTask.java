package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTask
{
	private static final String	JUST_A_STRING	= "Apa";
	private static final User	user			= new User("U", "Ser", "Password", "email", null);
	private static final User	other			= new User("U2", "Ser", "Password", "email", null);
	private Task				task;

	@Before
	public void setUp() throws Exception
	{
		task = new Task("1", "", "", false, 0, false, user);
	}

	@Test
	public final void testSetName()
	{
		task.setName(JUST_A_STRING);
		assertEquals(task.getName(), JUST_A_STRING);
	}

	@Test
	public final void testSetParent()
	{
		task.setParent(JUST_A_STRING);
		assertEquals(task.getParent(), JUST_A_STRING);
	}

	@Test
	public final void testSetCompleted()
	{
		task.setCompleted(true);
		assertTrue(task.getCompleted());
	}

	@Test
	public final void testSetOwner()
	{
		assertFalse(task.getOwner().equals(other));
		task.setOwner(other);
		assertTrue(task.getOwner().equals(other));
	}

	@Test
	public final void testEqualsObject()
	{
		Task x = new Task(JUST_A_STRING, JUST_A_STRING, JUST_A_STRING, false, 0, false, user);
		Task y = new Task(JUST_A_STRING, JUST_A_STRING, JUST_A_STRING, false, 0, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(null, JUST_A_STRING, JUST_A_STRING, false, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, null, JUST_A_STRING, false, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, null, false, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, JUST_A_STRING, true, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, JUST_A_STRING, false, 2, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, JUST_A_STRING, false, 0, true, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task("", JUST_A_STRING, JUST_A_STRING, false, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, "", JUST_A_STRING, false, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, "", false, 0, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, "", false, 0, false, other);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, "", false, 0, false, null);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		assertFalse(x.equals(null));
		assertTrue(x.equals(x));
		assertFalse(x.equals(""));

	}

}
