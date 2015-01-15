package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.HttpHeaders;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.ReportResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class TestReportResource
{
	private static EntityManagerFactory		emf				= Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>	myAuthenticator	= new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static User						user;

	private static User						otheruser;

	@SuppressWarnings("deprecation")
	@ClassRule
	public static final ResourceTestRule	resources		= ResourceTestRule
																	.builder()
																	.addResource(new ReportResource(emf))
																	.addProvider(
																			new ViewMessageBodyWriter(
																					new MetricRegistry()))
																	.addProvider(
																			new ContextInjectableProvider<HttpHeaders>(
																					HttpHeaders.class, null))
																	.addResource(myAuthenticator).build();

	@BeforeClass
	public static void beforeClass()
	{
		UserDAO userDAO = new UserDAO(emf);
		user = new User("admin", "Bob B", "password", "email", null);
		otheruser = new User("minion", "Do er", "password", "email", null);
		userDAO.add(user);
		userDAO.add(otheruser);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		emf.close();
	}

	@Test
	public void testGetMonthReport()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<div id=\"MonthReport\""));
		Assert.assertTrue(actual.contains("<div id=\"month\">December</div>"));
		Assert.assertTrue(actual.contains("<div id=\"year\">2014</div>"));
	}

	@Test
	public void testGetYearReport()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<div id=\"YearReport\""));
		Assert.assertTrue(actual.contains("<div id=\"year\">2014</div>"));
	}

	@Test
	public void testGetYearReport_wrongUser()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		try
		{
			resource.accept("text/html").get(String.class);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testGetMonthReport_wrongPassword()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "pissword"));
		try
		{
			resource.accept("text/html").get(String.class);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public void testGetMonthReport_otherUser()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		try
		{
			resource.accept("text/html").get(String.class);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

}
