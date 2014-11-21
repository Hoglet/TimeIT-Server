package test;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.dao.UserDAO;
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

}
