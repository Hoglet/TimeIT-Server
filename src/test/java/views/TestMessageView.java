package views;

import java.net.MalformedURLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.MessageView;

public class TestMessageView
{
	private static EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetCurrentUser() throws MalformedURLException
	{
		User user2 = new User("minion", "Do Er", "password", "email", null);
		MessageView taskAddedView = new MessageView(user2, "", "", "/", null);
		Assert.assertEquals(user2, taskAddedView.getCurrentUser());
	}

	@Test
	public final void testGetHeadline() throws MalformedURLException
	{
		String headline = "Headline";
		String text = "the text";
		String url = "/somewhere";
		User user2 = new User("minion", "Do Er", "password", "email", null);
		MessageView taskAddedView = new MessageView(user2, headline, text, url, null);
		Assert.assertEquals(headline, taskAddedView.getHeadline());
	}

	@Test
	public final void testGetText() throws MalformedURLException
	{
		String headline = "Headline";
		String text = "the text";
		String url = "/somewhere";
		User user2 = new User("minion", "Do Er", "password", "email", null);
		MessageView taskAddedView = new MessageView(user2, headline, text, url, null);
		Assert.assertEquals(text, taskAddedView.getText());
	}

	@Test
	public final void testGetUrl() throws MalformedURLException
	{
		String headline = "Headline";
		String text = "the text";
		String url = "/somewhere";
		User user2 = new User("minion", "Do Er", "password", "email", null);
		MessageView taskAddedView = new MessageView(user2, headline, text, url, null);
		Assert.assertEquals(url, taskAddedView.getUrl());
	}

}
