package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.BaseView;

public class TestBaseView
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
		User user = new User("minion", "Do Er", "password", "email", null);
		BaseView view = new BaseView("index.ftl", user);
		Assert.assertEquals(view.getCurrentUser(), user);
	}
}
