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
	private final DateTime		createTime		= new DateTime(1000 * 1000);
	private Task				task;

	@Before
	public void setUp() throws Exception
	{
		task = new Task(taskID, "", null, false, createTime, false, user);
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
	public final void serializeToJSON_withParent() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		UUID id = UUID.fromString("415a8737-b433-4a31-b85f-1a63e34eaddb");
		UUID parentID = UUID.fromString("6fd9a659-8834-4d31-a69c-1c6b601b8f50");
		Task parent = new Task(parentID, "parent", null, false, new DateTime(1000 * 1000), false, user);
		Task task = new Task(id, "Task1", parent, false, new DateTime(1000 * 1000), false, user);
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = MAPPER.writeValueAsString(task);
		Assert.assertEquals(fixture("fixtures/taskWithParent.json"), jsonString);
	}

	@Test
	public final void deserializeFromJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		UUID id = UUID.fromString("415a8737-b433-4a31-b85f-1a63e34eaddb");
		Task expected = new Task(id, "Task1", null, false, new DateTime(1000 * 1000), false, user);
		Task actual = MAPPER.readValue(fixture("fixtures/task.json"), Task.class);
		Assert.assertEquals("Name: ", expected.getName(), actual.getName());
		Assert.assertEquals("Deleted: ", expected.getDeleted(), actual.getDeleted());
		Assert.assertEquals("ID: ", expected.getID(), actual.getID());
		Assert.assertEquals("Owner: ", expected.getOwner().getUsername(), actual.getOwner().getUsername());
		Assert.assertEquals("Completed: ", expected.getCompleted(), actual.getCompleted());
		Assert.assertEquals("Changed: ", expected.getLastChange(), actual.getLastChange());
	}

	@Test
	public final void deserializeFromJSON_withParent() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		UUID id = UUID.fromString("415a8737-b433-4a31-b85f-1a63e34eaddb");
		UUID parentID = UUID.fromString("6fd9a659-8834-4d31-a69c-1c6b601b8f50");
		Task parent = new Task(parentID, "parent", null, false, new DateTime(1000 * 1000), false, user);
		Task expected = new Task(id, "Task1", parent, false, new DateTime(1000 * 1000), false, user);
		Task actual = MAPPER.readValue(fixture("fixtures/taskWithParent.json"), Task.class);
		Assert.assertEquals("Name: ", expected.getName(), actual.getName());
		Assert.assertEquals("Deleted: ", expected.getDeleted(), actual.getDeleted());
		Assert.assertEquals("ID: ", expected.getID(), actual.getID());
		Assert.assertEquals("Owner: ", expected.getOwner().getUsername(), actual.getOwner().getUsername());
		Assert.assertEquals("Parent: ", expected.getParent().getID(), actual.getParent().getID());
		Assert.assertEquals("Completed: ", expected.getCompleted(), actual.getCompleted());
		Assert.assertEquals("Changed: ", expected.getLastChange(), actual.getLastChange());
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
		task.setName(JUST_A_STRING);
		assertEquals(task.getName(), JUST_A_STRING);
		assertTrue(createTime.isBefore(task.getLastChange()));
	}

	@Test
	public final void testSetParent()
	{
		Task parent = new Task(parentID, "", null, false, DateTime.now(), false, user);
		task.setParent(parent);
		assertEquals(task.getParent(), parent);
		assertTrue(createTime.isBefore(task.getLastChange()));
	}

	@Test
	public final void testSetCompleted()
	{
		task.setCompleted(true);
		assertTrue(task.getCompleted());
		assertTrue(createTime.isBefore(task.getLastChange()));
	}

	@Test
	public final void testSetOwner()
	{
		task.setOwner(other);
		assertTrue(task.getOwner().equals(other));
		assertTrue(createTime.isBefore(task.getLastChange()));
	}

	@Test
	public final void testSetDeleted()
	{
		task.setDeleted(true);
		assertEquals(true, task.getDeleted());
		assertTrue(createTime.isBefore(task.getLastChange()));
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
