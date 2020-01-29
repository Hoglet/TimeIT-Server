package resources;

import io.dropwizard.testing.junit.ResourceTestRule;
import io.dropwizard.views.ViewMessageBodyWriter;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import se.solit.timeit.dao.LoginKeyDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.LoginKey;
import se.solit.timeit.entities.User;
import se.solit.timeit.resources.CredentialRecoveryResource;
import se.solit.timeit.utilities.Crypto;
import se.solit.timeit.utilities.Email;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

public class TestCredentialRecoveryResource
{

	private static EntityManagerFactory		emf			= Persistence.createEntityManagerFactory("test");

	private final static HttpSession		mockSession	= Mockito.mock(HttpSession.class);
	private static MockMailer				mockMailer	= new MockMailer();

	@ClassRule
	public static final ResourceTestRule	resources	= ResourceTestRule
																.builder()
																.addResource(
																		new CredentialRecoveryResource(emf, mockMailer))
																.addProvider(
																		new SessionInjectableProvider<HttpSession>(
																				HttpSession.class,
																				mockSession))
																.addProvider(
																		new ViewMessageBodyWriter(
																				new MetricRegistry(), ImmutableList.of(new FreemarkerViewRenderer())))
																.addProvider(
																		new ContextInjectableProvider<HttpHeaders>(
																				HttpHeaders.class, null)).build();

	static String							mailaddress	= "mepa@mail.org";

	private final static User				user		= new User("username", "tester", "password", mailaddress,
																null);
	private static LoginKeyDAO				loginKeyDAO;

	private static UserDAO					userDAO;


	// Collection to store attributes keys/values
	final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();     


	@BeforeClass
	public static void beforeClass() throws SQLException, AddressException
	{
		loginKeyDAO = new LoginKeyDAO(emf);
		userDAO = new UserDAO(emf);
		userDAO.add(user);
	}

	@After
	public void After()
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			em.getTransaction().begin();
			Query query = em.createQuery("DELETE FROM LoginKey l");
			query.executeUpdate();
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}

	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testRecover()
	{
		Client client = resources.client();
		WebResource resource = client.resource("/recover/");
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<H1>Recovering password</H1>"));
		Assert.assertTrue(actual.contains("<form method=\"POST\" action='/recover'"));
	}

	@Test
	public final void testRecoverPost() throws MessagingException
	{
		Client client = resources.client();
		WebResource resource = client.resource("/recover/");

		try
		{
			Form form = new Form();
			form.add("address", mailaddress);
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}

		Email capturedEmail = mockMailer.getEmail();
		EntityManager em = emf.createEntityManager();
		Query query = em.createQuery("SELECT l FROM LoginKey l");
		LoginKey result = (LoginKey) query.getSingleResult();
		Assert.assertTrue(capturedEmail.message.contains("/recover/" + result.getId().toString()));
	}

	@Test
	public final void testRecoverPost_fail1() throws MessagingException
	{
		Client client = resources.client();
		WebResource resource = client.resource("/recover/");

		// Mock setAttribute
		Mockito.doAnswer(new Answer<Void>() 
		{
		    @Override
		    public Void answer(InvocationOnMock invocation) throws Throwable 
		    {
		        String key = (String) invocation.getArguments()[0];
		        Object value = invocation.getArguments()[1];
		        attributes.put(key, value);
		        System.out.println("put attribute key="+key+", value="+value);
		        return null;
		    }
		}).when(mockSession).setAttribute(Mockito.anyString(), Mockito.anyObject());

		// Mock getAttribute
		Mockito.doAnswer(new Answer<Object>() {
		    @Override
		    public Object answer(InvocationOnMock invocation) throws Throwable {
		        String key = (String) invocation.getArguments()[0];
		        Object value = attributes.get(key);
		        System.out.println("get attribute value for key="+key+" : "+value);
		        return value;
		    }
		}).when(mockSession).getAttribute(Mockito.anyString());

		
		
		
		try
		{
			Form form = new Form();
			form.add("address", "wrong@address.org");
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}
		Assert.assertEquals("No user connected to wrong@address.org", mockSession.getAttribute("message"));
	}

	@Test
	public final void testChangePassword()
	{
		Client client = resources.client();
		LoginKey loginKey = new LoginKey(user);
		loginKeyDAO.add(loginKey);
		WebResource resource = client.resource("/recover/" + loginKey.getId());
		String actual = resource.accept("text/html").get(String.class);
		Assert.assertTrue(actual.contains("<h2>New password</h2>"));
		Assert.assertTrue(actual.contains("form method=\"POST\" action='/recover/" + loginKey.getId()));
	}

	@Test
	public final void testChangePassword_noKey()
	{
		Client client = resources.client();
		LoginKey loginKey = new LoginKey(user);
		WebResource resource = client.resource("/recover/" + loginKey.getId());
		try
		{
			resource.accept("text/html").get(String.class);
			Assert.fail("Should have thrown exception");
		}
		catch (UniformInterfaceException e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}
	}

	@Test
	public final void testChangePasswordPost() throws MessagingException
	{
		Client client = resources.client();
		LoginKey loginKey = new LoginKey(user);
		loginKeyDAO.add(loginKey);
		WebResource resource = client.resource("/recover/" + loginKey.getId());
		String password = "banana";

		try
		{
			Form form = new Form();
			form.add("password", password);
			resource.accept("text/html").post(String.class, form);
		}
		catch (Exception e)
		{
			Assert.assertEquals("Client response status: 303", e.getMessage());
		}
		User daUser = userDAO.getUser(user.getUsername());
		Assert.assertEquals(Crypto.encrypt(password), daUser.getPassword());

	}

}
