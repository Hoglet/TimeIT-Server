package resources;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;

import java.util.UUID;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.assertj.core.api.Fail;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;

import se.solit.timeit.application.MyAuthenticator;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.IndexResource;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;


public class TestIndexResource
{
	private static EntityManagerFactory     emf  = Persistence.createEntityManagerFactory("test");

	private final static HttpSession        mockSession	= Mockito.mock(HttpSession.class);

	@ClassRule
	public static final ResourceTestRule  resources = ResourceTestRule.builder()
			.addResource(new IndexResource(emf))
/*			.addProvider(
					new SessionInjectableProvider<HttpSession>(
							HttpSession.class,
							mockSession))
			.addProvider(
					new ViewMessageBodyWriter(
							new MetricRegistry(),
							ImmutableList.of(new FreemarkerViewRenderer())))
			.addProvider(
					new ContextInjectableProvider<HttpHeaders>(
							HttpHeaders.class, null))*/
			.addResource(AuthFactory.binder( new BasicAuthFactory<User>( new MyAuthenticator(emf), "TimeIT auth", User.class)))
			.build();

	
	
	
	@BeforeClass
	public static void beforeClass()
	{
		UserDAO userDAO = new UserDAO(emf);
		TaskDAO taskDAO = new TaskDAO(emf);
		User user = new User("admin", "Bob B", "password", "email", null);
		userDAO.add(user);
		Task task = new Task(UUID.randomUUID(), "admin stuff", null, user);
		taskDAO.add(task);
	}

	@AfterClass
	public static void afterClass()
	{
		UserDAO userDAO = new UserDAO(emf);
		User user = new User("admin", "Bob B", "password", "email", null);
		userDAO.delete(user);
		emf.close();
	}

	@Test
	public final void testIndexPage()
	{
		assertThat(resources.client().target("/").request().get().toString())
				.contains("admin stuff");

		//resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		//String actual = resource.accept("text/html").get(String.class);


	}

	@Test
	public final void testLandingPage()
	{
		assertThat(resources.client().target("/").request().get().toString())
				.contains("<H1>TimeIT server</H1>");
		//String actual = resource.accept("text/html").get(String.class);
		//Assert.assertTrue(actual.contains("<H1>TimeIT server</H1>"));
	}

	@Test
	public final void testLoginPage()
	{
		Fail.fail("Do test");
/*		assertThat(resources
				.client()
				.addFilter(new HTTPBasicAuthFilter("admin", "password"))
				.target("/login").request().get().toString())
				.contains("<H1>TimeIT server</H1>");
		WebResource resource = client.resource("/login");
		resource.addFilter(new HTTPBasicAuthFilter("admin", "password"));
		try
		{
			resource.accept("text/html").get(String.class);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals(UniformInterfaceException.class, e.getClass());
		}
*/
	}
}
