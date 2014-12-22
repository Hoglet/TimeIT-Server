package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.TaskAddedView;

public class TestTaskAddedView
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
		TaskAddedView taskAddedView = new TaskAddedView(user2);
		Assert.assertEquals(user2, taskAddedView.getCurrentUser());
	}

}
