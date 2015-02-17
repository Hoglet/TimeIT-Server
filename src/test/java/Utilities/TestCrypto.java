package Utilities;

import org.junit.Assert;
import org.junit.Test;

import se.solit.timeit.utilities.Crypto;

public class TestCrypto
{

	@Test
	public void testEncryptPassword()
	{
		Assert.assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
				Crypto.encrypt("password"));
		try
		{
			Crypto.encrypt("");
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals(IllegalArgumentException.class, e.getClass());
			Assert.assertEquals("String to encrypt cannot be null or zero length", e.getMessage());
		}
		try
		{
			Crypto.encrypt(null);
			Assert.fail("Should have thrown exception");
		}
		catch (Exception e)
		{
			Assert.assertEquals(IllegalArgumentException.class, e.getClass());
			Assert.assertEquals("String to encrypt cannot be null or zero length", e.getMessage());
		}

	}

}
