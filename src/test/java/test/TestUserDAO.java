package test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

public class TestUserDAO
{

	public EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private UserDAO				userdao;
	private User				user;

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

	@Test
	public final void testGetUsers()
	{

	}

	@Test
	public final void testGetUser()
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		userdao.add(user);
		User u2 = userdao.getUser(user.getUsername());
		assertEquals(u2, user);
	}

	@Test
	public final void testUpdate()
	{
		User user = new User("Test Tester", "tester", "password", "email", null);
		userdao.add(user);
		User u2 = new User("Test Tester", "tester", "better password", "email", null);
		userdao.update(u2);
		User u3 = userdao.getUser(user.getUsername());
		assertEquals(u3, u2);

	}

	@Test
	public final void testDelete()
	{

	}

}