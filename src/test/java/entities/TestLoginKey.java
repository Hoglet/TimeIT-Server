package entities;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.entities.LoginKey;
import se.solit.timeit.entities.User;

public class TestLoginKey
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	private final User	user	= new User("username", "Name", "password", "email", null);

	@Test
	public void testSetUser()
	{
		LoginKey key = new LoginKey(user);
		Assert.assertEquals(user.getUsername(), key.getUser().getUsername());
	}

}
