package test;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;

import com.google.common.base.Optional;

public class TestMyAuthenticator
{
	private static EntityManagerFactory	emf;
	private static User					user;

	@BeforeClass
	public static void beforeClass()
	{
		emf = Persistence.createEntityManagerFactory("test");
		UserDAO userDAO = new UserDAO(emf);
		user = new User("tester", "Test Testman", "password", "email", null);
		userDAO.add(user);
	}

	@AfterClass
	public static void afterClass()
	{
		UserDAO userDAO = new UserDAO(emf);
		userDAO.delete(user);
		emf.close();
	}

	@Test
	public final void testAuthenticate() throws AuthenticationException
	{
		MyAuthenticator authenticator = new MyAuthenticator(emf);
		BasicCredentials credentials = new BasicCredentials("tester", "password");
		Object expected = Optional.of(user);
		Assert.assertEquals(expected, authenticator.authenticate(credentials));
	}

	@Test
	public final void testAuthenticate_wrongPassword() throws AuthenticationException
	{
		MyAuthenticator authenticator = new MyAuthenticator(emf);
		BasicCredentials credentials = new BasicCredentials("tester", "pword");
		Assert.assertEquals(Optional.absent(), authenticator.authenticate(credentials));
	}

	@Test
	public final void testAuthenticate_wrongUser() throws AuthenticationException
	{
		MyAuthenticator authenticator = new MyAuthenticator(emf);
		BasicCredentials credentials = new BasicCredentials("agda", "password");
		Assert.assertEquals(Optional.absent(), authenticator.authenticate(credentials));
	}

}
