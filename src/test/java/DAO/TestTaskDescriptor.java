package DAO;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.TaskDescriptor;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TestTaskDescriptor
{
	private static User           owner            = new User("owner", "Owner", "password", "email", null);
	private static ZonedDateTime  changeTime       = ZonedDateTime.now();
	private static UUID           parentID         = UUID.fromString("13060e94-8b31-4f38-9f7e-8a709db57408");
	private static UUID           childID          = UUID.randomUUID();
	private static TaskDescriptor taskDescriptor1;
	private static TaskDescriptor taskDescriptor2;
	private static Task           parent;
	private static Task           child;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		parent = new Task(parentID, "parent", null, false, changeTime, false, owner);
		child = new Task(childID, "child", parent, false, changeTime, false, owner);
		taskDescriptor1 = new TaskDescriptor(parent);
		taskDescriptor2 = new TaskDescriptor(child);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Test
	public void testGetIndentString()
	{
		assertEquals("&nbsp;&nbsp;&nbsp;", taskDescriptor2.getIndentString());
	}

	@Test
	public void testGetTask()
	{
		assertEquals(parent, taskDescriptor1.getTask());
	}

	@Test
	public void testGetId()
	{
		assertEquals("13060e94-8b31-4f38-9f7e-8a709db57408", taskDescriptor1.getId());
	}

	@Test
	public void testGetName()
	{
		assertEquals("parent", taskDescriptor1.getName());
	}

}
