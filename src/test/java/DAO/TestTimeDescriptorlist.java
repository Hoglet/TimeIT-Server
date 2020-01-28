package DAO;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTimeDescriptorlist
{

	private static TimeDescriptorList list;
	private static User               owner        = new User("owner", "Owner", "password", "email", null);
	private static UUID               parentID     = UUID.randomUUID();
	private static UUID               childID      = UUID.randomUUID();
	private static Duration           duration;
	private static Duration           durationWithChildren;
	private static Task               parent;
	private static Task               child;
	private static UUID               parent2ID    = UUID.randomUUID();
	private static Task               parent2;
	private static UUID               child2ID     = UUID.randomUUID();
	private static Task               child2;
	private static TimeDescriptor     tdToFind;
	private final UUID                grandchildID = UUID.randomUUID();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		parent = new Task(parentID, "parent", null, false, false, owner);
		parent2 = new Task(parent2ID, "parent2", null, false, false, owner);
		child = new Task(childID, "child", parent, false, false, owner);
		child2 = new Task(child2ID, "child", parent2, false, false, owner);
		duration = Duration.ofSeconds(70);
		durationWithChildren = Duration.ofSeconds(140);
		list = new TimeDescriptorList();
		tdToFind = new TimeDescriptor(parent2, duration, durationWithChildren);
		list.add(new TimeDescriptor(parent, duration, durationWithChildren));
		list.add(new TimeDescriptor(child, duration, durationWithChildren));
		list.add(new TimeDescriptor(child, duration, durationWithChildren));
		list.add(tdToFind);
		list.add(new TimeDescriptor(child2, duration, durationWithChildren));
	}

	@Test
	public void testFind()
	{
		TimeDescriptor found = list.find(parent2);
		assertEquals(tdToFind, found);

		Task oddTask = new Task(UUID.randomUUID(), "Oddy", null, false, false, owner);
		found = list.find(oddTask);
		assertEquals(null, found);

	}

	@Test
	public void testSortedAddTimeDescriptor()
	{
		Task grandChild = new Task(grandchildID, "grandChild", child, false, false, owner);
		TimeDescriptor td_toAdd = new TimeDescriptor(grandChild, duration, durationWithChildren);
		list.add(td_toAdd);
		StringBuilder sb = new StringBuilder();
		for (TimeDescriptor td : list)
		{
			sb.append(td.getTask().getName());
			sb.append("\n");
		}
		String expected = "parent\nchild\ngrandChild\nparent2\nchild\n";
		String result = sb.toString();
		assertEquals(expected, result);

		try
		{
			list.add(td_toAdd);
		}
		catch (Exception e)
		{
			assertEquals(Exception.class, e.getClass());
		}

	}

}
