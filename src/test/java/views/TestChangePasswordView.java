package views;

import java.sql.SQLException;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.ChangePasswordView;

public class TestChangePasswordView
{

	private static EntityManagerFactory	emf			= Persistence.createEntityManagerFactory("test");
	private static User					user		= new User("minion", "Do Er", "password", "email", null);

	private final static HttpSession	mockSession	= Mockito.mock(HttpSession.class);

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGetTime()
	{
		String key = UUID.randomUUID().toString();
		ChangePasswordView view = new ChangePasswordView(user, key, null, mockSession);
		Assert.assertEquals(view.getTemporaryKey(), key);
	}

	@Test
	public final void testGetUser()
	{
		String key = UUID.randomUUID().toString();
		ChangePasswordView view = new ChangePasswordView(user, key, null, mockSession);
		Assert.assertEquals(view.getUser().getUsername(), user.getUsername());
	}

}
