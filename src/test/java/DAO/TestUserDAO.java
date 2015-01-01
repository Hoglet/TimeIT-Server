package DAO;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
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
	public final void testDelete() throws SQLException
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		userdao.add(user);
		TaskDAO taskdao = new TaskDAO(emf);
		DateTime now = DateTime.now();
		Task task = new Task(UUID.randomUUID(), "parent", null, false, now, false, user);
		taskdao.add(task);
		UUID timeID = UUID.randomUUID();
		Time time = new Time(timeID, new DateTime(0), new DateTime(100 * 1000), false, now, task);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);
		userdao.delete(user);
	}

}
