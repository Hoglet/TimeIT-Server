package entities;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.util.UUID;

import org.joda.time.DateTime;
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
	private final UUID			taskID			= UUID.randomUUID();
	private final UUID			parentID		= UUID.randomUUID();

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public final void serializeToJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		UUID id = UUID.fromString("415a8737-b433-4a31-b85f-1a63e34eaddb");
		Task task = new Task(id, "Task1", null, false, new DateTime(1000 * 1000), false, user);
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = MAPPER.writeValueAsString(task);
		Assert.assertEquals(fixture("fixtures/task.json"), jsonString);
	}

	@Test
	public final void forceID()
	{
		try
		{
			Task task = new Task(null, "", null, false, DateTime.now(), false, user);
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
		Task task = new Task(taskID, "", null, false, DateTime.now(), false, user);
		task.setName(JUST_A_STRING);
		assertEquals(task.getName(), JUST_A_STRING);
	}

	@Test
	public final void testSetParent()
	{
		Task task = new Task(taskID, "", null, false, DateTime.now(), false, user);
		Task parent = new Task(parentID, "", null, false, DateTime.now(), false, user);
		task.setParent(parent);
		assertEquals(task.getParent(), parent);
	}

	@Test
	public final void testSetCompleted()
	{
		Task task = new Task(taskID, "", null, false, DateTime.now(), false, user);
		task.setCompleted(true);
		assertTrue(task.getCompleted());
	}

	@Test
	public final void testSetOwner()
	{
		Task task = new Task(taskID, "", null, false, DateTime.now(), false, user);
		task.setOwner(other);
		assertTrue(task.getOwner().equals(other));
	}

	@Test
	public final void testEqualsObject()
	{
		DateTime now = DateTime.now();
		Task x = new Task(taskID, JUST_A_STRING, null, false, now, false, user);
		Task y = new Task(taskID, JUST_A_STRING, null, false, now, false, user);
		Task parent = new Task(parentID, "parent", null, false, now, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(taskID, null, null, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, parent, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, true, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, false, new DateTime(0), false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, false, now, true, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(UUID.randomUUID(), JUST_A_STRING, null, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, "", null, false, now, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new Task(taskID, JUST_A_STRING, parent, false, now, false, user);
		y = new Task(taskID, JUST_A_STRING, parent, false, now, false, user);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, false, now, false, other);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new Task(taskID, null, null, false, now, false, user);
		y = new Task(taskID, null, null, false, now, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new Task(taskID, JUST_A_STRING, null, false, now, false, user);
		y = new Task(taskID, JUST_A_STRING, null, false, now, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		assertFalse(x.equals(null));
		assertTrue(x.equals(x));
		assertFalse(x.equals(""));

	}
}
