package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import java.sql.SQLException;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.HttpHeaders;

import org.joda.time.DateTime;
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
	private static final UUID				timeID			= UUID.randomUUID();
	private static final UUID				taskID			= UUID.randomUUID();

	private static EntityManagerFactory		emf				= Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>	myAuthenticator	= new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static TimeDAO					timeDAO;

	private static User						user;

	private static Time						time;
	private static Task						task;
	private static DateTime					now				= DateTime.now();

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
		time = new Time(timeID, new DateTime(0), new DateTime(1000), false, now, task);
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
		Assert.assertTrue(actual.contains("<h2>Add time</h2>"));
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

	@Test
	public final void testAddPostPage() throws SQLException
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		UUID id = UUID.randomUUID();
		DateTime start = now.withSecondOfMinute(0).withMillisOfSecond(0);
		DateTime stop = new DateTime(start.plus(60000));

		Time expected = new Time(id, start, stop, false, stop, task);

		Form form = new Form();
		form.add("timeid", id);
		form.add("date", start.toString("yyy-MM-dd"));

		form.add("start", start.toString("HH:mm"));
		form.add("stop", stop.toString("HH:mm"));
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

		String name = "Banarne";
		Form form = new Form();
		form.add("timeid", timeID);
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
