package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.HttpHeaders;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TimeResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class TestTimeResource
{
	private static final String				timeID			= "12";
	private static final String				taskID			= "1";

	private static EntityManagerFactory		emf				= Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>	myAuthenticator	= new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static TimeDAO					timeDAO;

	private static User						user;

	private static Time						time;
	private static Task						task;
	private static Date						now				= new Date();

	@ClassRule
	public static final ResourceTestRule	resources		= ResourceTestRule
																	.builder()
																	.addResource(new TimeResource(emf))
																	.addProvider(
																			new ViewMessageBodyWriter(
																					new MetricRegistry()))
																	.addProvider(
																			new ContextInjectableProvider<HttpHeaders>(
																					HttpHeaders.class, null))
																	.addResource(myAuthenticator).build();

	@BeforeClass
	public static void beforeClass() throws SQLException
	{
		UserDAO userDAO = new UserDAO(emf);
		timeDAO = new TimeDAO(emf);
		TaskDAO taskDAO = new TaskDAO(emf);
		user = new User("admin", "Bob B", "password", "email", null);
		userDAO.add(user);
		task = new Task(taskID, "Task", null, false, now, false, user);
		taskDAO.add(task);
		time = new Time(timeID, new Date(0), new Date(1000), false, new Date(), task);
		timeDAO.add(time);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testAddPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<h1>Add time</h1>"));
		Assert.assertTrue(actual.contains("<form method=\"POST\" action='/time/add'"));
	}

	@Test
	public final void testAddPageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pissword"));
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

	@SuppressWarnings("deprecation")
	@Test
	public final void testAddPostPage() throws SQLException
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String id = UUID.randomUUID().toString();
		Date start = new Date();
		Date stop = new Date(start.getTime() + 1000);
		start.setSeconds(0);
		stop.setSeconds(0);
		Time expected = new Time(id, start, stop, false, stop, task);

		String DATE_FORMAT = "yyyy-MM-dd";
		SimpleDateFormat dateFormater = new SimpleDateFormat(DATE_FORMAT);
		String TIME_FORMAT = "HH:mm";
		SimpleDateFormat timeFormater = new SimpleDateFormat(TIME_FORMAT);
		timeFormater.setTimeZone(TimeZone.getDefault());

		Form form = new Form();
		form.add("timeid", id);
		form.add("date", dateFormater.format(start));

		form.add("start", timeFormater.format(start));
		form.add("stop", timeFormater.format(stop));
		form.add("taskid", task.getID());

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Time actual = timeDAO.getByID(id);
		Assert.assertEquals(expected.getID(), actual.getID());
		Assert.assertEquals(expected.getStart().toString(), actual.getStart().toString());
		Assert.assertEquals(expected.getStop().toString(), actual.getStop().toString());
		Assert.assertEquals(expected.getTask().getID(), actual.getTask().getID());
	}

	@Test
	public final void testAddPostPageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pissword"));

		String id = timeID;
		String name = "Banarne";
		Form form = new Form();
		form.add("timeid", id);
		form.add("parent", null);
		form.add("name", name);

		try
		{
			resource.accept("text/html").post(String.class, form);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}
}
