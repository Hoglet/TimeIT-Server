package resources;

import static org.junit.Assert.fail;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.UserResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

public class TestUserResource
{
	private static EntityManagerFactory     emf             = Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>  myAuthenticator = new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static UserDAO                  userDAO;

	private static User                     admin;
	private User                            minion;
	private final static HttpSession        mockSession     = Mockito.mock(HttpSession.class);

	@ClassRule
	public static final ResourceTestRule  resources = ResourceTestRule.builder()
	                                                                  .addResource(new UserResource(emf))
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

	private ZoneId        zone  = ZonedDateTime.now().getZone();
	private ZonedDateTime start = Instant.ofEpochSecond(0).atZone(zone);
	private ZonedDateTime stop  = Instant.ofEpochSecond(100).atZone(zone);

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
		WebResource resource = client.resource("/user/");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		Mockito.when(mockSession.getAttribute("message")).thenReturn("apa");

		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("Bob B"));
	}

	@Test
	public final void testAdmin_failAccess()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/");
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
	public final void testUserEditGet_Self()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/minion");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

		String actual = resource.accept("text/html").get(String.class);

		Assert.assertTrue(actual.contains("Bob C"));
		Assert.assertTrue(actual.contains("action='/user/minion'"));
	}

	@Test
	public final void testUserEditGet_OtherUserWithRights()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/minion");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String actual = resource.accept("text/html").get(String.class);

		Assert.assertTrue(actual.contains("Bob C"));
		Assert.assertTrue(actual.contains("action='/user/minion'"));
	}

	@Test
	public final void testUserEditGet_otherUserWithoutRights()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/admin");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

		try
		{
			resource.accept("text/html").get(String.class);
			Assert.fail("Should have denied access");
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
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
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
	public final void testUserEdit_retainOldPassword()
	{
		Client client = resources.client();

		String username = minion.getUsername();
		String name = "Banarne";
		String password = "Password";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		User expected = new User(username, name, password, email, roles2);
		Form form = new Form();
		form.add("submitType", "save");
		form.add("name", name);
		form.add("password", "");
		form.add("email", email);
		form.put("roles", roles);

		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(minion.getPassword(), actual.getPassword());
		Assert.assertEquals(expected.getEmail(), actual.getEmail());
		Assert.assertEquals("Active roles:", 1, actual.getRoles().size());

	}

	@Test
	public final void testUserEdit_personalEditingSave()
	{
		Client client = resources.client();

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
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getPassword(), actual.getPassword());
		Assert.assertEquals(expected.getEmail(), actual.getEmail());
		Assert.assertEquals("Active roles:", 0, actual.getRoles().size());

	}

	@Test
	public final void testUserEdit_personalEditingSaveOfOtherUser()
	{
		String username = admin.getUsername();
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		Form form = new Form();
		form.add("submitType", "save");
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		Client client = resources.client();
		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
		try
		{
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		User actual = userDAO.getUser(username);
		Assert.assertEquals(admin, actual);
	}

	@Test
	public final void testUserEdit_notAuthorized()
	{

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
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		Client client = resources.client();
		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
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
		WebResource resource = client.resource("/user/add");
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
	public final void testUserAdd_withoutRights()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/add");
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
		form.add("submitType", "save");
		form.add("userName", username);
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		try
		{
			resource.accept("text/html").post(String.class, form);
			Assert.fail("Should have thrown an Exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public final void testUserDelete() throws SQLException
	{

		String username = "minion";
		User expected = userDAO.getUser(username);
		Assert.assertEquals(minion, expected);

		Task task = new Task(UUID.randomUUID(), "parent", null, false, ZonedDateTime.now(), true, minion);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);

		UUID timeID = UUID.randomUUID();
		Time time = new Time(timeID, start, stop, false, ZonedDateTime.now(), task);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);

		Form form = new Form();
		List<String> users = new ArrayList<String>();
		users.add(username);
		form.add("submitType", "OK");
		form.put("userSelector", users);

		Client client = resources.client();
		WebResource resource = client.resource("/user/delete/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
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
	public final void testUserDelete_withoutRights() throws SQLException
	{
		String username = "minion";
		User expected = userDAO.getUser(username);
		Assert.assertEquals(minion, expected);

		Task task = new Task(UUID.randomUUID(), "parent", null, false, ZonedDateTime.now(), true, minion);
		TaskDAO taskdao = new TaskDAO(emf);
		taskdao.add(task);

		UUID timeID = UUID.randomUUID();
		Time time = new Time(timeID, start, stop, false, ZonedDateTime.now(), task);
		TimeDAO timedao = new TimeDAO(emf);
		timedao.add(time);

		Form form = new Form();
		List<String> users = new ArrayList<String>();
		users.add(username);
		form.add("submitType", "OK");
		form.put("userSelector", users);

		Client client = resources.client();
		WebResource resource = client.resource("/user/delete/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
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
	public final void testUserAdd_notAuthorized()
	{
		String username = "apa";
		String name = "Banarne";
		String password = "Pasvord";
		String email = "emajl";
		List<String> roles = new ArrayList<String>();
		roles.add(Role.ADMIN);
		Collection<Role> roles2 = new ArrayList<Role>();
		roles2.add(new Role(Role.ADMIN));
		Form form = new Form();
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		Client client = resources.client();
		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));
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
		form.add("name", name);
		form.add("password", password);
		form.add("email", email);
		form.put("roles", roles);

		Client client = resources.client();
		WebResource resource = client.resource("/user/" + username);
		resource.addFilter(new HTTPBasicAuthFilter("admin", "pword"));
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
	public final void testUserEditPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/minion");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("class=\"tab selected\"><h2>Edit</h2>"));
		Assert.assertTrue(actual.contains("<td>minion</td>"));
	}

	@Test
	public final void testUserAddPage()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/add");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("class=\"tab selected\"><h2>Add user</h2>"));
	}

	@Test
	public final void testUserAddPage_withoutRights()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/add");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

		try
		{
			resource.accept("text/html").get(String.class);
			Assert.fail("Should have thrown an exceptoion");
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 401", e.getMessage());
		}
	}

	@Test
	public final void testUserDeleteConfirm()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/delete/minion");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));

		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("action='/user/delete/minion'"));
	}

	@Test
	public final void testUserDeleteConfirm_withoutRights()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/user/delete/minion");
		resource.addFilter(new HTTPBasicAuthFilter("minion", "password"));

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
