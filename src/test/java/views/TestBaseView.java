package views;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.entities.User;
import se.solit.timeit.views.BaseView;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;

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
		HttpContext context = Mockito.mock(HttpContext.class);
		HttpRequestContext mockRequest = Mockito.mock(HttpRequestContext.class);
		Mockito.when(mockRequest.getPath()).thenReturn("report/");
		Mockito.when(context.getRequest()).thenReturn(mockRequest);

		HttpSession session = Mockito.mock(HttpSession.class);
		BaseView view = new BaseView("index.ftl", user, context, session);
		Assert.assertEquals("selected", view.getClasses("report"));

		Mockito.when(mockRequest.getPath()).thenReturn("/");
		view = new BaseView("index.ftl", user, context, session);
		Assert.assertEquals("", view.getClasses("report"));

	}
}
