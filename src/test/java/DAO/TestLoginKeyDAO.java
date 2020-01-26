package DAO;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.ZonedDateTime;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

	public  static EntityManagerFactory  emf   = Persistence.createEntityManagerFactory("test");
	private final static User            user  = new User("username", "Name", "password", "email", null);
	private static LoginKeyDAO           keydao;

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
		field.set(loginKey, ZonedDateTime.now().minusDays(3).toInstant().getEpochSecond());

		keydao.add(loginKey);
		keydao.removeOld(Duration.ofDays(1));
		LoginKey actual = keydao.getByID(loginKey.getId());
		Assert.assertEquals(null, actual);
	}

}
