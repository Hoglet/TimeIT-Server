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
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TestTime
{
	private static final User	owner	= new User("123", "", "", "", null);
	private static final Task	task1	= new Task("1", "task1", null, false, new Date(), false, owner);
	private static final Task	task2	= new Task("2", "task2", null, false, new Date(), false, owner);
	private Time				time;

	@Before
	public void setUp() throws Exception
	{
		time = new Time("1234", new Date(0), new Date(1 * 1000), false, new Date(), task1);
	}

	@Test
	public final void serializeToJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		Task task = new Task("123", "Task1", null, false, new Date(100 * 1000), false, user);
		Time time = new Time("uuid", new Date(10 * 1000), new Date(100 * 1000), false, new Date(100 * 1000), task);
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
		Date now = new Date();
		time.setChanged(now);
		assertEquals(time.getChanged(), now);
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
		Date now = new Date();
		time.setStart(now);
		assertEquals(time.getStart(), now);
	}

	@Test
	public final void testSetStop()
	{
		Date now = new Date();
		time.setStop(now);
		assertEquals(time.getStop(), now);
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
		Date start = new Date(0);
		Date stop = new Date(1 * 1000);
		Date now = new Date();
		Time x = new Time(null, start, stop, false, now, task1);
		Time y = new Time(null, start, stop, false, now, task1);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		x = new Time("1234", start, stop, false, now, task1);
		y = new Time("1234", start, stop, false, now, task1);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Time(null, start, stop, false, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", stop, stop, false, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", start, now, false, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", start, stop, true, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", start, stop, false, new Date(42), task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time("1234", start, stop, false, now, task2);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		assertTrue(x.equals(x));
		assertFalse(y.equals(""));
		assertFalse(y.equals(null));

	}

}
