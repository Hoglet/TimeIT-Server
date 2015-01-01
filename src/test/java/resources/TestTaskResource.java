package resources;

import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

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
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.TaskResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class TestTaskResource
{
	private static final String				taskID			= "12";

	private static EntityManagerFactory		emf				= Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>	myAuthenticator	= new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static TaskDAO					taskDAO;

	private static User						user;

	private static Task						task;

	@ClassRule
	public static final ResourceTestRule	resources		= ResourceTestRule
																	.builder()
																	.addResource(new TaskResource(emf))
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
		taskDAO = new TaskDAO(emf);
		user = new User("admin", "Bob B", "password", "email", null);
		userDAO.add(user);
		task = new Task(taskID, "admin stuff", null, false, DateTime.now(), false, user);
		taskDAO.add(task);
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
		WebResource resource = client.resource("/task/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("admin stuff"));
		Assert.assertTrue(actual.contains("<h1>Add task</h1>"));
		Assert.assertTrue(actual.contains("<form method=\"POST\" action='/task/add'"));
	}

	@Test
	public final void testEditPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/edit?taskid=" + taskID);
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("admin stuff"));
		Assert.assertTrue(actual.contains("<h1>Edit task</h1>"));
		Assert.assertTrue(actual.contains("<form method=\"POST\" action='/task/edit'"));
	}

	@Test
	public final void testAddPageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/add");
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
	public final void testAddPostPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String id = UUID.randomUUID().toString();
		String name = "Banarne";
		Task expected = new Task(id, name, null, false, DateTime.now(), false, user);
		Form form = new Form();
		form.add("taskid", id);
		form.add("parent", null);
		form.add("name", name);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Task actual = taskDAO.getByID(id);
		Assert.assertEquals(expected.getID(), actual.getID());
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getParent(), actual.getParent());
	}

	@Test
	public final void testAddPostPageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pissword"));

		String id = taskID;
		String name = "Banarne";
		Form form = new Form();
		form.add("taskid", id);
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

	@Test
	public final void testAddPostPage2()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String id = UUID.randomUUID().toString();
		String name = "Banarne";
		Task expected = new Task(id, name, task, false, DateTime.now(), false, user);
		Form form = new Form();
		form.add("taskid", id);
		form.add("parent", task.getID());
		form.add("name", name);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Task actual = taskDAO.getByID(id);
		Assert.assertEquals(expected.getID(), actual.getID());
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getParent(), actual.getParent());
	}

	@Test
	public final void testPostEditPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String id = taskID;
		String name = "Banarne";
		Task expected = new Task(id, name, null, false, DateTime.now(), false, user);
		Form form = new Form();
		form.add("taskid", id);
		form.add("parent", null);
		form.add("name", name);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Task actual = taskDAO.getByID(id);
		Assert.assertEquals(expected.getID(), actual.getID());
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getParent(), actual.getParent());
	}

	@Test
	public final void testPostEditPage2()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String parentID = UUID.randomUUID().toString();
		Task parent = new Task(parentID, "Parent", null, false, DateTime.now(), false, user);
		taskDAO.add(parent);
		String id = taskID;
		String name = "Banarne";
		Task expected = new Task(id, name, parent, false, DateTime.now(), false, user);
		Form form = new Form();
		form.add("taskid", id);
		form.add("parent", parent.getID());
		form.add("name", name);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Task actual = taskDAO.getByID(id);
		Assert.assertEquals(expected.getID(), actual.getID());
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getParent().toString(), actual.getParent().toString());
	}

	@Test
	public final void testPostEditPageAuth()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task/edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pissword"));

		String id = UUID.randomUUID().toString();
		String name = "Banarne";
		Form form = new Form();
		form.add("taskid", id);
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

	@Test
	public final void testChooserPage_edit()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task?action=edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		resource = resource.queryParam("action", "edit");
		String result = resource.accept("text/html").get(String.class);
		Assert.assertTrue(result.contains("<form method=\"GET\" action='/task/edit'"));
	}

	@Test
	public final void testChooserPage_delete()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/task?action=delete");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		resource = resource.queryParam("action", "delete");
		String result = resource.accept("text/html").get(String.class);
		Assert.assertTrue(result.contains("<form method=\"POST\" action='/task/delete'"));
	}

	@Test
	public final void testDelete()
	{
		String id = UUID.randomUUID().toString();
		Task task2delete = new Task(id, "name", null, false, DateTime.now(), false, user);
		taskDAO.add(task2delete);
		Client client = resources.client();
		WebResource resource = client.resource("/task/delete");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		Form form = new Form();
		form.add("taskid", id);

		String result = resource.accept("text/html").post(String.class, form);
		Assert.assertTrue(result.contains("Task is deleted"));
		Assert.assertEquals(true, taskDAO.getByID(id).getDeleted());
	}

	@Test
	public final void testDelete_auth()
	{
		String id = UUID.randomUUID().toString();
		Task task2delete = new Task(id, "name", null, false, DateTime.now(), false, user);
		taskDAO.add(task2delete);
		Client client = resources.client();
		WebResource resource = client.resource("/task/delete");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pissword"));

		Form form = new Form();
		form.add("taskid", id);

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
