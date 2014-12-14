package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.IndexView;

public class TestIndexView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetCurrentUser()
	{
		User user2 = new User("minion", "Do Er", "password", "email", null);
		IndexView indexView = new IndexView(user2, emf);
		Assert.assertEquals(indexView.getCurrentUser(), user2);
	}

}
