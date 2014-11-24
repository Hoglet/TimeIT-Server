package views;

import org.junit.Assert;
import org.junit.Test;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.IndexView;

public class TestIndexView
{

	@Test
	public final void testGetCurrentUser()
	{
		User user2 = new User("minion", "Do Er", "password", "email", null);
		IndexView indexView = new IndexView(user2);
		Assert.assertEquals(indexView.getCurrentUser(), user2);
	}

}
