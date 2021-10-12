package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.BaseView;

import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

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
		HttpSession session = Mockito.mock(HttpSession.class);
		User user = new User("minion", "Do Er", "password", "email", null);
		BaseView view = new BaseView("index.ftl", user, null, session);
		Assert.assertEquals(view.getCurrentUser(), user);
	}

	@Test
	public final void testGetClassess()
	{
		User user = new User("minion", "Do Er", "password", "email", null);
		var uriInfo = Mockito.mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("report/");

		HttpSession session = Mockito.mock(HttpSession.class);
		BaseView view = new BaseView("index.ftl", user, uriInfo, session);
		Assert.assertEquals("selected", view.getClasses("report"));

		view = new BaseView("index.ftl", user, uriInfo, session);
		Assert.assertEquals("", view.getClasses("report"));

	}
}
