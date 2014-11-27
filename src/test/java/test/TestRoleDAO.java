package test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import se.solit.timeit.dao.RoleDAO;
import se.solit.timeit.entities.Role;

public class TestRoleDAO
{

	public static EntityManagerFactory	emf		= Persistence.createEntityManagerFactory("test");
	private final RoleDAO				roledao	= new RoleDAO(emf);
	private final Role					role	= new Role("TEST");
	private final EntityManager			em		= emf.createEntityManager();

	@Before
	public void setUp() throws Exception
	{

	}

	@After
	public void tearDown() throws Exception
	{
		em.getTransaction().begin();
		TypedQuery<Role> getQuery = em.createQuery("SELECT u FROM Role u",
				Role.class);
		List<Role> roles = getQuery.getResultList();
		for (Role role : roles)
		{
			em.remove(role);
		}
		em.getTransaction().commit();
	}

	@AfterClass
	public static void afterClass()
	{
		emf.close();
	}

	@Test
	public final void testGet()
	{
		roledao.add(role);
		Role r2 = roledao.get("TEST");
		assertEquals(r2, role);
	}

	@Test
	public final void testGetRoles()
	{
		Collection<Role> roles = roledao.getRoles();
		assertEquals(roles.size(), 0);
		Role r2 = new Role("TEST");
		roledao.add(r2);
		roles = roledao.getRoles();
		assertEquals(roles.size(), 1);
	}

}
