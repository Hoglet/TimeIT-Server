package DAO;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

public class TestUserDAO
{

	public static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private UserDAO						userdao;

	@Before
	public void setUp() throws Exception
	{
		userdao = new UserDAO(emf);
	}

	@After
	public void tearDown()
	{
		Collection<User> users = userdao.getUsers();
		for (User user : users)
		{
			userdao.delete(user);
		}
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testInvalidUser()
	{
		User user = new User("", "tester", "password", "email", null);
		try
		{
			userdao.add(user);
			Assert.fail("Zero length username should not be allowed");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Username must have non zero length", e.getMessage());
		}
	}

	@Test
	public final void testGetUser()
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		userdao.add(user);
		User u2 = userdao.getUser(user.getUsername());
		Assert.assertEquals(u2, user);
	}

	@Test
	public final void testUpdate()
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		userdao.add(user);
		User u2 = new User("Test Tester", "tester", "better password", "email", null);
		userdao.update(u2);
		User u3 = userdao.getUser(user.getUsername());
		Assert.assertEquals(u3, u2);
	}

	@Test
	public final void testUpdateOfNonExisting()
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		try
		{
			userdao.update(user);
			Assert.fail("Non existing user should not be added by update");
		}
		catch (Exception e)
		{
			Assert.assertEquals("User does not exist", e.getMessage());
		}
	}

	@Test
	public final void testDelete() throws SQLException
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		userdao.add(user);
		TaskDAO taskdao = new TaskDAO(emf);
		ZonedDateTime now = ZonedDateTime.now();
		Task task = new Task(UUID.randomUUID(), "parent", null, false, now, false, user);
		taskdao.add(task);
		UUID timeID = UUID.randomUUID();
		
		ZonedDateTime start = Instant.ofEpochSecond(0).atZone(ZoneId.of("UTC"));
		ZonedDateTime stop  = Instant.ofEpochSecond(100).atZone(ZoneId.of("UTC"));
		Time time = new Time(timeID, start, stop, false, now, task);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);
		userdao.delete(user);
	}

	@Test
	public final void testGetUserByEmail()
	{
		User user = new User("Test Tester", "tester", "password", "email@main.com", null);
		userdao.add(user);
		User u2 = userdao.getByEMail("email@main.com");
		Assert.assertEquals(user, u2);

		u2 = userdao.getByEMail("noOne@main.com");
		Assert.assertEquals(null, u2);
	}

}
