package entities;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestTask
{
	private static final String  JUST_A_STRING = "Apa";
	private static final User    user          = new User("U", "Ser", "Password", "email", null);
	private static final User    other         = new User("U2", "Ser", "Password", "email", null);
	private final UUID           taskID        = UUID.randomUUID();
	private final UUID           parentID      = UUID.randomUUID();
	private final Instant        createTime    = Instant.ofEpochSecond(1000);
	private final Instant        epoch         = Instant.ofEpochSecond(0);
	private Task                 task;

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
		Task task = new Task(id, "Task1", null, false, createTime, false, user);
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
		Task parent = new Task(parentID, "parent", null, false, createTime, false, user);
		Task task = new Task(id, "Task1", parent, false, createTime, false, user);
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
		Task expected = new Task(id, "Task1", null, false, createTime, false, user);
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
		Task parent = new Task(parentID, "parent", null, false, createTime, false, user);
		Task expected = new Task(id, "Task1", parent, false, createTime, false, user);
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
			Task task = new Task(null, "", null, false, false, user);
			Assert.assertTrue("Should not allow null user", false);
			task.withCompleted(true);
		}
		catch (Exception e)
		{
			Assert.assertEquals(NullPointerException.class, e.getClass());
		}
	}

	@Test
	public final void testSetName()
	{
		Task newTask = task.withName(JUST_A_STRING);
		assertEquals(newTask.getName(), JUST_A_STRING);
		assertTrue(createTime.compareTo(newTask.getLastChange()) < 0);
	}

	@Test
	public final void testSetParent()
	{
		Task parent = new Task(parentID, "", null, false, false, user);
		Task newTask = task.withParent(parent);
		assertEquals(newTask.getParent(), parent);
		assertTrue(createTime.isBefore(newTask.getLastChange()));
	}

	@Test
	public final void testSetCompleted()
	{
		Task newTask = task.withCompleted(true);
		assertTrue(newTask.getCompleted());
		assertTrue(createTime.isBefore(newTask.getLastChange()));
	}

	@Test
	public final void testSetOwner()
	{
		Task newTask = task.withOwner(other);
		assertTrue(newTask.getOwner().equals(other));
		assertTrue(createTime.isBefore(newTask.getLastChange()));
	}

	@Test
	public final void testSetDeleted()
	{
		Task newTask = task.withDeleted(true);
		assertEquals(true, newTask.getDeleted());
		assertTrue(createTime.isBefore(newTask.getLastChange()));
	}

	@Test
	public final void testEqualsObject()
	{
		Task x = new Task(taskID, JUST_A_STRING, null, false, false, user);
		Task y = new Task(taskID, JUST_A_STRING, null, false, false, user);
		Task parent = new Task(parentID, "parent", null, false, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(taskID, null, null, false, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, parent, false, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, true, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, false, epoch, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, false, true, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(UUID.randomUUID(), JUST_A_STRING, null, false, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Task(taskID, "", null, false, false, user);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new Task(taskID, JUST_A_STRING, parent, false, false, user);
		y = new Task(taskID, JUST_A_STRING, parent, false, false, user);
		assertTrue(x.equals(y));
		assertTrue(y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Task(taskID, JUST_A_STRING, null, false, false, other);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		x = new Task(taskID, null, null, false, false, user);
		y = new Task(taskID, null, null, false, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new Task(taskID, JUST_A_STRING, null, false, false, user);
		y = new Task(taskID, JUST_A_STRING, null, false, false, user);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		assertFalse(x.equals(null));
		assertTrue(x.equals(x));
		assertFalse(x.equals(""));

	}
}
