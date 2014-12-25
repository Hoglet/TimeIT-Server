package entities;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestTask
{
	private static final String	JUST_A_STRING	= "Apa";
	private static final User	user			= new User("U", "Ser", "Password", "email", null);
	private static final User	other			= new User("U2", "Ser", "Password", "email", null);

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public final void serializeToJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		Task task = new Task("123", "Task1", null, false, new Date(1000 * 1000), false, user);
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = MAPPER.writeValueAsString(task);
		Assert.assertEquals(fixture("fixtures/task.json"), jsonString);
	}

	@Test
	public final void forceID()
	{
		try
		{
			Task task = new Task(null, "", null, false, new Date(), false, user);
			Assert.assertTrue("Should not allow null user", false);
			task.setCompleted(true);
		}
		catch (Exception e)
		{
			Assert.assertEquals(NullPointerException.class, e.getClass());
		}
	}

	@Test
	public final void testSetName()
	{
		Task task = new Task("123", "", null, false, new Date(), false, user);
		task.setName(JUST_A_STRING);
		assertEquals(task.getName(), JUST_A_STRING);
	}

	@Test
	public final void testSetParent()
	{
		Task task = new Task("123", "", null, false, new Date(), false, user);
		Task parent = new Task("124", "", null, false, new Date(), false, user);
		task.setParent(parent);
		assertEquals(task.getParent(), parent);
	}

	@Test
	public final void testSetCompleted()
	{
		Task task = new Task("123", "", null, false, new Date(), false, user);
		task.setCompleted(true);
		assertTrue(task.getCompleted());
	}

	@Test
	public final void testSetOwner()
	{
		Task task = new Task("123", "", null, false, new Date(), false, user);
		task.setOwner(other);
		assertTrue(task.getOwner().equals(other));
	}

	@Test
	public final void testEqualsObject()
	{
		Date now = new Date();
		Task x = new Task(JUST_A_STRING, JUST_A_STRING, null, false, now, false, user);
		Task y = new Task(JUST_A_STRING, JUST_A_STRING, null, false, now, false, user);
		Task parent = new Task("parent", "parent", null, false, now, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, null, null, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, parent, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, null, true, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, null, false, new Date(0), false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, null, false, now, true, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task("", JUST_A_STRING, null, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, "", null, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new Task(JUST_A_STRING, JUST_A_STRING, parent, false, now, false, user);
		y = new Task(JUST_A_STRING, JUST_A_STRING, parent, false, now, false, user);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(JUST_A_STRING, JUST_A_STRING, null, false, now, false, other);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new Task(JUST_A_STRING, null, null, false, now, false, user);
		y = new Task(JUST_A_STRING, null, null, false, now, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new Task(JUST_A_STRING, JUST_A_STRING, null, false, now, false, user);
		y = new Task(JUST_A_STRING, JUST_A_STRING, null, false, now, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		assertFalse(x.equals(null));
		assertTrue(x.equals(x));
		assertFalse(x.equals(""));

	}
}
