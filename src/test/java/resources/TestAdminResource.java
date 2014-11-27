package resources;

import static org.junit.Assert.fail;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.ws.rs.core.HttpHeaders;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import se.solit.timeit.MyAuthenticator;
import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.AdminResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class TestAdminResource
{
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User> myAuthenticator = new BasicAuthProvider<User>(new MyAuthenticator(emf),
			"Authenticator");

	private static UserDAO userDAO;

	private static User admin;
	private User minion;

	@ClassRule
	public static final ResourceTestRule resources = ResourceTestRule.builder().addResource(new AdminResource(emf))
			.addProvider(new ViewMessageBodyWriter(new MetricRegistry()))
			.addProvider(new ContextInjectableProvider<HttpHeaders>(HttpHeaders.class, null))
			.addResource(myAuthenticator).build();

	@BeforeClass
	public static void beforeClass()
	{
		userDAO = new UserDAO(emf);
		RoleDAO roleDAO = new RoleDAO(emf);
		roleDAO.add(new Role(Role.ADMIN));
		Collection<Role> roles = new ArrayList<Role>();
		roles.add(new Role(Role.ADMIN));
		admin = new User("admin", "Bob B", "password", "email", roles);
		userDAO.add(admin);
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Before
	public void setUp()
	{
		minion = new User("minion", "Bob C", "password", "email", null);
		userDAO.add(minion);
	}

	@After
	public void tearDown()
	{
		try
		{
			userDAO.delete(minion);
		}
		catch (EntityNotFoundException e)
		{

		}
	}

	@Test
	public final void testAdmin()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("Bob B"));
	}

	@Test
	public final void testAdmin_failAccess()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		try
		{
			resource.accept("text/html").get(String.class);
			fail("Should be denied");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public final void testUserEdit()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/edit");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String username = minion.getUsername();
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		User expected = new User(username, name, password, email, roles2);
		Form form = new Form();
		form.add("submitType", "save");
		form.add("userName", username);
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertTrue(expected.equals(actual));
	}

	@Test
	public final void testUserEdit_notAuthorized()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/edit");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

		String username = minion.getUsername();
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		User expected = new User(username, name, password, email, roles2);
		Form form = new Form();
		form.add("submitType", "save");
		form.add("userName", username);
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertFalse(expected.equals(actual));
	}

	@Test
	public final void testUserAdd()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String username = "apa";
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		User expected = new User(username, name, password, email, roles2);
		Form form = new Form();
		form.add("submitType", "save");
		form.add("userName", username);
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertTrue(expected.equals(actual));
	}

	@Test
	public final void testUserDelete()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String username = "minion";
		Form form = new Form();
		List<String> users = new ArrayList<String>();
		users.add(username);
		form.add("submitType", "OK");
		form.put("userSelector", users);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertTrue(null == actual);
	}

	@Test
	public final void testUserAdd_notAuthorized()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/add");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

		String username = "apa";
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		Form form = new Form();
		form.add("userName", username);
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}
	}

	@Test
	public final void testUserAdd_wrongCredentials()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pword"));

		String username = "apa";
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		Form form = new Form();
		form.add("submitType", "save");
		form.add("userName", username);
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public final void testUserPage_failAccess()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		try
		{
			resource.accept("text/html").post(String.class, null);
			fail("Should be denied");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public final void testUserPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		Form form = new Form();
		form.add("userSelector", "");
		form.add("submitType", "");

		String actual = resource.accept("text/html").post(String.class, form);
		Assert.assertTrue(actual.contains("Bob B"));
	}

	@Test
	public final void testUserEditPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		Form form = new Form();
		List<String> users = new ArrayList<String>();
		users.add("minion");
		form.add("submitType", "edit");
		form.put("userSelector", users);

		String actual = resource.accept("text/html").post(String.class, form);
		Assert.assertTrue(actual.contains("Edit settings for user minion"));
	}

	@Test
	public final void testUserAddPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/admin/user/");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		Form form = new Form();
		List<String> users = new ArrayList<String>();
		users.add("minion");
		form.add("submitType", "add");
		form.put("userSelector", users);

		String actual = resource.accept("text/html").post(String.class, form);
		Assert.assertTrue(actual.contains("<h1>Add user</h1>"));
	}

}