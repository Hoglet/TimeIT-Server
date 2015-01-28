package DAO;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTimeDescriptorlist
{

	private static TimeDescriptorList	list;
	private static User					owner			= new User("owner", "Owner", "password", "email", null);
	private static DateTime				changeTime		= DateTime.now();
	private static UUID					parentID		= UUID.randomUUID();
	private static UUID					childID			= UUID.randomUUID();
	private static Duration				duration;
	private static Duration				durationWithChildren;
	private static Task					parent;
	private static Task					child;
	private static UUID					parent2ID		= UUID.randomUUID();
	private static Task					parent2;
	private static UUID					child2ID		= UUID.randomUUID();
	private static Task					child2;
	private final UUID					grandchildID	= UUID.randomUUID();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		parent = new Task(parentID, "parent", null, false, changeTime, false, owner);
		parent2 = new Task(parent2ID, "parent2", null, false, changeTime, false, owner);
		child = new Task(childID, "child", parent, false, changeTime, false, owner);
		child2 = new Task(child2ID, "child", parent2, false, changeTime, false, owner);
		duration = new Duration(70000);
		durationWithChildren = new Duration(140000);
		list = new TimeDescriptorList();
		list.add(new TimeDescriptor(parent, duration, durationWithChildren));
		list.add(new TimeDescriptor(child, duration, durationWithChildren));
		list.add(new TimeDescriptor(child, duration, durationWithChildren));
		list.add(new TimeDescriptor(parent2, duration, durationWithChildren));
		list.add(new TimeDescriptor(child2, duration, durationWithChildren));
	}

	@Test
	public void testSortedAddTimeDescriptor()
	{
		Task grandChild = new Task(grandchildID, "grandChild", child, false, changeTime, false, owner);
		list.add(new TimeDescriptor(grandChild, duration, durationWithChildren));
		StringBuilder sb = new StringBuilder();
		for (TimeDescriptor td : list)
		{
			sb.append(td.getTask().getName());
			sb.append("\n");
		}
		String expected = "parent\nchild\ngrandChild\nparent2\nchild\n";
		String result = sb.toString();
		assertEquals(expected, result);
	}

}
