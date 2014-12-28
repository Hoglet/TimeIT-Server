package DAO;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

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
		Task task = new Task("123", "parent", null, false, new Date(), false, user);
		taskdao.add(task);
		Time time = new Time("12", new Date(0), new Date(100 * 1000), false, new Date(), task);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);
		userdao.delete(user);
	}

}
