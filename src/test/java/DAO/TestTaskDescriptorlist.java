package DAO;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDescriptor;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTaskDescriptorlist
{

	private static TaskDescriptorList	list;
	private static User					owner			= new User("owner", "Owner", "password", "email", null);
	private static DateTime				changeTime		= DateTime.now();
	private static UUID					parentID		= UUID.randomUUID();
	private static UUID					childID			= UUID.randomUUID();
	private static Task					parent;
	private static Task					child;
	private static UUID					parent2ID		= UUID.randomUUID();
	private static Task					parent2;
	private static UUID					child2ID		= UUID.randomUUID();
	private static Task					child2;
	private static TaskDescriptor		tdToFind;
	private final UUID					grandchildID	= UUID.randomUUID();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		parent = new Task(parentID, "parent", null, false, changeTime, false, owner);
		parent2 = new Task(parent2ID, "parent2", null, false, changeTime, false, owner);
		child = new Task(childID, "child", parent, false, changeTime, false, owner);
		child2 = new Task(child2ID, "child", parent2, false, changeTime, false, owner);
		list = new TaskDescriptorList();
		list.add(new TaskDescriptor(child2));
		tdToFind = new TaskDescriptor(parent2);
		list.add(new TaskDescriptor(parent));
		list.add(new TaskDescriptor(child));
		list.add(tdToFind);
	}

	@Test
	public void testFind()
	{
		TaskDescriptor found = list.find(parent2);
		assertEquals(tdToFind.getId(), found.getId());

		Task oddTask = new Task(UUID.randomUUID(), "Oddy", null, false, changeTime, false, owner);
		found = list.find(oddTask);
		assertEquals(null, found);

	}

	@Test
	public void testSortedAddTaskDescriptor() throws IOException
	{
		Task grandChild = new Task(grandchildID, "grandChild", child, false, changeTime, false, owner);
		TaskDescriptor tdToAdd = new TaskDescriptor(grandChild);
		list.add(tdToAdd);
		StringBuilder sb = new StringBuilder();
		for (TaskDescriptor td : list)
		{
			sb.append(td.getIndentString());
			sb.append(td.getTask().getName());
			sb.append("\n");
		}
		String expected = "parent2\n&nbsp;&nbsp;&nbsp;child\nparent\n&nbsp;&nbsp;&nbsp;child\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;grandChild\n";
		// String expected = fixture("fixtures/taskTree.txt");
		String result = sb.toString();
		assertEquals(expected, result);

		try
		{
			list.add(tdToAdd);
		}
		catch (Exception e)
		{
			assertEquals(IllegalArgumentException.class, e.getClass());
		}

	}

}
