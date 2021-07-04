package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TimeResource;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class TestTimeResource
{
	private static final UUID            timeID = UUID.randomUUID();
	private static final UUID            taskID = UUID.randomUUID();
	private static EntityManagerFactory  emf = Persistence.createEntityManagerFactory("test");
	private static TimeDAO               timeDAO;
	private static User                  user;
	private final static HttpSession     mockSession  = Mockito.mock(HttpSession.class);

	private static Time                  time;
	private static Task                  task;
	private static Instant               now           = Instant.now();
	private static ZoneId                zone          = ZonedDateTime.now().getZone();
	private final  static String         comment       = "Just a comment";
	private final  DateTimeFormatter     dateFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
	private final  DateTimeFormatter     timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	private static BasicAuthProvider<User>  myAuthenticator  = new BasicAuthProvider<User>(new MyAuthenticator(emf),  "Authenticator");

	@ClassRule
	public static final ResourceTestRule    resources  = ResourceTestRule.builder()
	                                                                     .addResource(new TimeResource(emf))
	                                                                     .addProvider(
	                                                                           new SessionInjectableProvider<HttpSession>(
	                                                                                 HttpSession.class,
	                                                                                 mockSession)
	                                                                                  )
	                                                                     .addProvider(
	                                                                           new ViewMessageBodyWriter(
	                                                                                 new MetricRegistry(), ImmutableList.of(new FreemarkerViewRenderer())))
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
		task = new Task(taskID, "Task", null, false, false, user);
		taskDAO.add(task);
		
		Instant start = Instant.ofEpochSecond(0);
		Instant stop = Instant.ofEpochSecond(1);
		time = new Time(timeID, start, stop, false, now, task, comment);
		timeDAO.add(time);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testEditTimePage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/edit/" + timeID.toString());
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<h2>Edit time</h2>"));
		Assert.assertTrue(actual.contains("<form method=\"POST\" action='/time/edit'"));
	}

	@Test
	public final void testEditTimePageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/edit/" + timeID.toString());
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
	public final void testEditTimePostPage() throws SQLException
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		Mockito.when(mockSession.getAttribute("returnPoint")).thenReturn("/");
		ZonedDateTime start = now.atZone(zone).withSecond(0);
		ZonedDateTime stop = start.plusSeconds(60);

		Time expected = new Time(timeID, start.toInstant(), stop.toInstant(), false, stop.toInstant(), task, comment);

		Form form = new Form();
		form.add("timeid", timeID.toString());		
		form.add("date", start.format(dateFormatter));
		form.add("start", start.format(timeFormatter));
		form.add("stop", stop.format(timeFormatter));
		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Time actual = timeDAO.getByID(timeID);
		Assert.assertEquals(expected.getID(), actual.getID());
		Assert.assertEquals(expected.getStart().toString(), actual.getStart().toString());
		Assert.assertEquals(expected.getStop().toString(), actual.getStop().toString());
		Assert.assertEquals(expected.getTask().getID(), actual.getTask().getID());
	}

	@Test
	public final void testEditTimePostPageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/time/edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pissword"));

		ZonedDateTime start = now.atZone(zone);
		ZonedDateTime stop = start.plusSeconds(60);

		Form form = new Form();
		form.add("timeid", timeID.toString());
		form.add("date", start.format(dateFormatter));
		form.add("start", start.format(timeFormatter));
		form.add("stop", stop.format(timeFormatter));

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
		ZonedDateTime start = now.atZone(zone).withSecond(0).withNano(0);
		ZonedDateTime stop = start.plusSeconds(60);

		Time expected = new Time(id, start.toInstant(), stop.toInstant(), false, stop.toInstant(), task, comment);

		Form form = new Form();
		form.add("timeid", id);
		form.add("date", start.format(dateFormatter));

		form.add("start", start.format(timeFormatter));
		form.add("stop", stop.format(timeFormatter));
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
