package test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.DatabaseConfiguration;

public class TestDatabaseConfiguration
{

	private static final String		JUST_A_STRING	= "apa";
	private DatabaseConfiguration	dbConf;

	@Before
	public void setUp() throws Exception
	{
		dbConf = new DatabaseConfiguration();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public final void testSetDriverClass()
	{
		dbConf.setDriverClass(JUST_A_STRING);
		String actual = dbConf.getDriverClass();
		Assert.assertEquals(JUST_A_STRING, actual);
	}

	@Test
	public final void testSetUser()
	{
		dbConf.setUser(JUST_A_STRING);
		String actual = dbConf.getUser();
		Assert.assertEquals(JUST_A_STRING, actual);
	}

	@Test
	public final void testSetPassword()
	{
		dbConf.setPassword(JUST_A_STRING);
		String actual = dbConf.getPassword();
		Assert.assertEquals(JUST_A_STRING, actual);
	}

	@Test
	public final void testSetUrl()
	{
		dbConf.setUrl(JUST_A_STRING);
		String actual = dbConf.getUrl();
		Assert.assertEquals(JUST_A_STRING, actual);
	}

}
