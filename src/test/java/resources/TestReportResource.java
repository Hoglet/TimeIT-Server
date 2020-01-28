package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import java.util.Locale;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.ReportResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class TestReportResource
{
	private static EntityManagerFactory      emf              = Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>   myAuthenticator  = new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static User                      user;
	private static String                    taskID;
	private static User                      otheruser;

	private final static HttpSession         mockSession      = Mockito.mock(HttpSession.class);

	@SuppressWarnings("deprecation")
	@ClassRule
	public static final ResourceTestRule     resources  = ResourceTestRule.builder()
	                                                                      .addResource(new ReportResource(emf))
	                                                                      .addProvider(
	                                                                                 new SessionInjectableProvider<HttpSession>(
	                                                                                        HttpSession.class,
	                                                                                        mockSession))
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
		Locale.setDefault(new Locale("en", "UK"));
		UserDAO userDAO = new UserDAO(emf);
		user = new User("admin", "Bob B", "password", "email", null);
		otheruser = new User("minion", "Do er", "password", "email", null);
		userDAO.add(user);
		userDAO.add(otheruser);
		taskID = UUID.randomUUID().toString();
		Task task = new Task(UUID.fromString(taskID), "TaskName", null, false, false, user);
		TaskDAO taskDAO = new TaskDAO(emf);
		taskDAO.add(task);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		emf.close();
	}

	@Test
	public void testDayReport()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12/1");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<div id=\"DayReport\""));
		Assert.assertTrue(actual.contains("<div id=\"month\">December</div>"));
		Assert.assertTrue(actual.contains("<div id=\"year\">2014</div>"));
	}

	@Test
	public void testGetDayReport_wrongUser()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12/1");
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

	@Test
	public void testTaskDetailReport()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12/1/" + taskID);
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<div id=\"DetailsReport\""));
		Assert.assertTrue(actual.contains("<div id=\"month\">December</div>"));
		Assert.assertTrue(actual.contains("<div id=\"year\">2014</div>"));
		Assert.assertTrue(actual.contains("<h2> TaskName </h2>"));
	}

	@Test
	public void testTaskDetailReport_wrongUser()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/report/minion/2014/12/1/" + taskID);
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

}
