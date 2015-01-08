package resources;

import static org.junit.Assert.fail;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;

import java.util.ArrayList;
import java.util.Collection;

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

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Role;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.AdminResource;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class TestAdminResource
{
	private static EntityManagerFactory		emf				= Persistence.createEntityManagerFactory("test");

	private static BasicAuthProvider<User>	myAuthenticator	= new BasicAuthProvider<User>(new MyAuthenticator(emf),
																	"Authenticator");

	private static UserDAO					userDAO;

	private static User						admin;
	private User							minion;

	@ClassRule
	public static final ResourceTestRule	resources		= ResourceTestRule
																	.builder()
																	.addResource(new AdminResource(emf))
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

}
