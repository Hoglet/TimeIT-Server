package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.UserAdminView;

public class TestAdminView
{
	public static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static UserAdminView		adminView;
	private static User					user2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		User user1 = new User("minion", "The doer", "password", "email", null);
		user2 = new User("admin", "The boss", "password", "email", null);
		UserDAO userDao = new UserDAO(emf);
		userDao.add(user1);
		userDao.add(user2);
		adminView = new UserAdminView(emf, user2, null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		emf.close();
	}

	@Test
	public final void testGetUsers()
	{
		Assert.assertEquals(adminView.getUsers().size(), 2);
	}

}
