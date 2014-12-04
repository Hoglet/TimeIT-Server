package entities;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestTime
{
	private static final User	owner	= new User("123", "", "", "", null);
	private static final Task	task1	= new Task("1", "task1", null, false, 0, false, owner);
	private static final Task	task2	= new Task("2", "task2", null, false, 0, false, owner);
	private Time				time;

	@Before
	public void setUp() throws Exception
	{
		time = new Time("1234", 0, 1, false, 0, task1);
	}

	@Test
	public final void serializeToJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		Task task = new Task("123", "Task1", null, false, 1000, false, user);
		Time time = new Time("uuid", 10, 100, false, 100, task);
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = MAPPER.writeValueAsString(time);
		Assert.assertEquals(fixture("fixtures/time.json"), jsonString);
	}

	@Test
	public final void testGetUUID()
	{
		assertEquals(time.getID(), "1234");
	}

	@Test
	public final void testSetChanged()
	{
		time.setChanged(3);
		assertEquals(time.getChanged(), 3);
	}

	@Test
	public final void testSetTask()
	{
		assertFalse(time.getTask().equals(task2));
		time.setTask(task2);
		assertTrue(time.getTask().equals(task2));
	}

	@Test
	public final void testSetStart()
	{
		time.setStart(123);
		assertEquals(time.getStart(), 123);
	}

	@Test
	public final void testSetStop()
	{
		time.setStop(124);
		assertEquals(time.getStop(), 124);
	}

	@Test
	public final void testSetDeleted()
	{
		assertFalse(time.getDeleted());
		time.setDeleted(true);
		assertTrue(time.getDeleted());
	}

	@Test
	public final void testEqualsObject()
	{
		Time x = new Time(null, 0, 1, false, 0, task1);
		Time y = new Time(null, 0, 1, false, 0, task1);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new Time("1234", 0, 1, false, 0, task1);
		y = new Time("1234", 0, 1, false, 0, task1);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Time(null, 0, 1, false, 0, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", 1, 1, false, 0, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", 0, 2, false, 0, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", 0, 1, true, 0, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", 0, 1, false, 1, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", 0, 1, false, 0, task2);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		assertTrue(x.equals(x));
		assertFalse(y.equals(""));
		assertFalse(y.equals(null));

	}

}
