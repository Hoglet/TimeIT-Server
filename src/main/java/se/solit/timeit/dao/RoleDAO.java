package se.solit.timeit.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import se.solit.timeit.entities.Role;

public class RoleDAO
{
	private final EntityManagerFactory	entityManagerFactory;

	public RoleDAO(EntityManagerFactory emf)
	{
		entityManagerFactory = emf;
	}

	public final void add(final Role role)
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		try
		{
			em.getTransaction().begin();
			em.persist(role);
			em.getTransaction().commit();
		}
		finally
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			em.close();
		}
	}

	public final Role get(final String string)
	{

		Role role = null;
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		role = em.find(Role.class, string);
		em.getTransaction().commit();
		em.close();
		return role;
	}

	@SuppressWarnings("unchecked")
	public Collection<Role> getRoles()
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		Collection<Role> result = em.createQuery("SELECT u FROM Role u").getResultList();
		em.close();
		return result;
	}

}
