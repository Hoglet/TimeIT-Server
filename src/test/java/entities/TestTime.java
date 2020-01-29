package entities;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

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
	private static final UUID     timeID       = UUID.fromString("a9e104e7-fd86-4953-a297-97736fc939fe");
	private static final Instant  creationTime = Instant.ofEpochSecond(1000);
	private static final Instant  epoch        = Instant.ofEpochSecond(0);
	private static final Instant  start        = Instant.ofEpochSecond(10);
	private static final Instant  stop         = Instant.ofEpochSecond(100);
	private static final Instant  changeTime   = Instant.ofEpochSecond(100);

	private static final User     owner        = new User("123", "", "password", "", null);
	private static final Task     task1        = new Task(UUID.randomUUID(), "task1", null, false, creationTime, false, owner);
	private static final Task     task2        = new Task(UUID.randomUUID(), "task2", null, false, creationTime, false, owner);
	private Time                  time;

	@Before
	public void setUp() throws Exception
	{
		time = new Time(timeID, epoch, epoch.plusSeconds(1), false, creationTime, task1);
	}

	@Test
	public final void serializeToJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		UUID id = UUID.fromString("a9e104e7-fd86-4953-a297-97736fc939fe");
		Task task = new Task(id, "Task1", null, false, changeTime, false, user);

		Time time = new Time(timeID, start, stop, false, changeTime, task);
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonString = MAPPER.writeValueAsString(time);
		Assert.assertEquals(fixture("fixtures/time.json"), jsonString);
	}

	@Test
	public final void deserializeFromJSON() throws IOException
	{
		ObjectMapper MAPPER = Jackson.newObjectMapper();
		User user = new User("testman", "Test Tester", "password", "", null);
		UUID id = UUID.fromString("a9e104e7-fd86-4953-a297-97736fc939fe");
		Task task = new Task(id, "Task1", null, false, changeTime, false, user);

		Time time = new Time(timeID, start, stop, false, changeTime, task);
		Time result = MAPPER.readValue(fixture("fixtures/time2.json"), Time.class);
		Assert.assertEquals(time.getDeleted(), result.getDeleted());
		Assert.assertEquals(time.getID(), result.getID());
		Assert.assertEquals(time.getStart(), result.getStart());
		Assert.assertEquals(time.getStop(), result.getStop());
		Assert.assertEquals(time.getChanged(), result.getChanged());
	}

	@Test
	public final void testGetUUID()
	{
		assertEquals(time.getID(), timeID);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public final void testEqualsObject()
	{
		Instant start = epoch;
		Instant stop = start.plusSeconds(1);
		Instant now  = Instant.now();

		Time x = new Time(timeID, start, stop, false, now, task1);
		Time y = new Time(timeID, start, stop, false, now, task1);
		assertTrue(x.equals(y) && y.equals(x));
		assertTrue(x.hashCode() == y.hashCode());

		y = new Time(UUID.randomUUID(), start, stop, false, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time(timeID, stop, stop, false, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time(timeID, start, now, false, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time(timeID, start, stop, true, now, task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time(timeID, start, stop, false, Instant.ofEpochSecond(42), task1);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		y = new Time(timeID, start, stop, false, now, task2);
		assertFalse(x.equals(y));
		assertFalse(y.equals(x));
		assertFalse(x.hashCode() == y.hashCode());

		assertTrue(x.equals(x));
		assertFalse(y.equals(""));
		assertFalse(y.equals(null));

	}
	
	@Test
	public final void testWithStart()
	{
		Instant newStart = Instant.ofEpochSecond(1);
		Time newTime = time.withStart(newStart);
		assertFalse(newTime.equals(time));
		assertNotEquals(newStart, time.getStart());
		assertEquals(newStart, newTime.getStart());
	}
	
	@Test
	public final void testWithStop()
	{
		Instant newStop = Instant.ofEpochSecond(2);
		Time newTime = time.withStop(newStop);
		assertFalse(newTime.equals(time));
		assertNotEquals(newStop, time.getStop());
		assertEquals(newStop, newTime.getStop());
	}
}
