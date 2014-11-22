package test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.DatabaseConfiguration;
import se.solit.timeit.TimeITConfiguration;

public class TestTimeITConfiguration
{

	private static final String	JUST_A_STRING	= "bepa";
	private TimeITConfiguration	timeITConfiguration;

	@Before
	public void setUp() throws Exception
	{
		timeITConfiguration = new TimeITConfiguration();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public final void testSetDefaultName()
	{
		timeITConfiguration.setDefaultName(JUST_A_STRING);
		String actual = timeITConfiguration.getDefaultName();
		Assert.assertEquals(JUST_A_STRING, actual);

	}

	@Test
	public final void testSetDatabase()
	{
		DatabaseConfiguration database = new DatabaseConfiguration();
		database.setDriverClass(JUST_A_STRING);
		database.setPassword(JUST_A_STRING);
		database.setUrl(JUST_A_STRING);
		database.setUser(JUST_A_STRING);
		timeITConfiguration.setDatabase(database);
		DatabaseConfiguration actual = timeITConfiguration.getDatabase();
		Assert.assertEquals(database, actual);
	}

}
