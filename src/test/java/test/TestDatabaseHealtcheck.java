package test;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.DatabaseHealthCheck;
import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;

import com.codahale.metrics.health.HealthCheck.Result;

public class TestDatabaseHealtcheck
{
	private final EntityManagerFactory	emf	= Persistence.createEntityManagerFactory("test");	;

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		RoleDAO roleDao = new RoleDAO(emf);
		Collection<Role> roles = roleDao.getRoles();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for (Role role : roles)
		{
			em.remove(em.getReference(Role.class, role.getName()));
		}
		em.getTransaction().commit();
	}

	@Test
	public final void testCheck_noDataInDatabase() throws Exception
	{
		DatabaseHealthCheck hc = new DatabaseHealthCheck(emf);
		Assert.assertEquals(Result.unhealthy("No roles in database"), hc.check());
	}

	@Test
	public final void testCheck_noDatabase() throws Exception
	{
		RoleDAO roleDao = new RoleDAO(emf);
		roleDao.add(new Role("Admin"));
		DatabaseHealthCheck hc = new DatabaseHealthCheck(emf);
		Assert.assertEquals(Result.healthy(), hc.check());
	}
}
