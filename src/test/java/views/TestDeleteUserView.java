package views;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;
import se.solit.timeit.views.DeleteUserView;

public class TestDeleteUserView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");
	private static User					user;

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		user = new User("minion", "Do Er", "password", "email", null);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);

	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetUser() throws SQLException
	{
		DeleteUserView view = new DeleteUserView(emf, user, user.getUsername());
		Assert.assertEquals(user, view.getUser());
	}

}
