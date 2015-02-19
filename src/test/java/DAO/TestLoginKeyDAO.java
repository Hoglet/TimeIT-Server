package DAO;

import java.lang.reflect.Field;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.solit.timeit.dao.LoginKeyDAO;
import se.solit.timeit.dao.UserDAO;
import se.solit.timeit.entities.LoginKey;
import se.solit.timeit.entities.User;

public class TestLoginKeyDAO
{

	public static EntityManagerFactory	emf						= Persistence.createEntityManagerFactory("test");
	private static LoginKeyDAO			keydao;
	private final static User			user					= new User("username", "Name", "password", "email",
																		null);
	private static final long			MILLISECONDS_PER_SECOND	= 1000;

	@BeforeClass
	public static void setUp() throws Exception
	{
		keydao = new LoginKeyDAO(emf);
		UserDAO userdao = new UserDAO(emf);
		userdao.add(user);
	}

	@After
	public void tearDown()
	{
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testAdd()
	{
		LoginKey loginKey = new LoginKey(user);
		keydao.add(loginKey);
		LoginKey actual = keydao.getByID(loginKey.getId());
		Assert.assertEquals(loginKey.getId(), actual.getId());
	}

	@Test
	public final void testManualDelete() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException
	{
		LoginKey loginKey = new LoginKey(user);

		keydao.add(loginKey);
		keydao.delete(loginKey);
		LoginKey actual = keydao.getByID(loginKey.getId());
		Assert.assertEquals(null, actual);
	}

	@Test
	public final void testRemoveOld() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException
	{
		LoginKey loginKey = new LoginKey(user);

		Field field = loginKey.getClass().getDeclaredField("lastChange");
		field.setAccessible(true);
		field.set(loginKey, DateTime.now().minusDays(3).getMillis() / MILLISECONDS_PER_SECOND);

		keydao.add(loginKey);
		keydao.removeOld(Duration.standardDays(1));
		LoginKey actual = keydao.getByID(loginKey.getId());
		Assert.assertEquals(null, actual);
	}

}
