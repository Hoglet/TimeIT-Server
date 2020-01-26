package se.solit.timeit.dao;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;


import se.solit.timeit.entities.LoginKey;

public class LoginKeyDAO
{

	private final EntityManagerFactory	emf;

	public LoginKeyDAO(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	public LoginKey getByID(UUID uuid)
	{
		EntityManager em = emf.createEntityManager();
		LoginKey loginKey = em.find(LoginKey.class, uuid.toString());
		em.close();
		return loginKey;
	}

	public void add(LoginKey loginKey)
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			em.getTransaction().begin();
			em.persist(loginKey);
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}

	}

	public void delete(LoginKey loginKey)
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			em.getTransaction().begin();
			em.remove(em.getReference(LoginKey.class, loginKey.getId().toString()));
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}
	}

	public void removeOld(Duration duration)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("DELETE FROM LoginKey k WHERE k.lastChange < :point");
		ZonedDateTime pointInTime = ZonedDateTime.now().minus(duration);
		query.setParameter("point", Instant.from(pointInTime).getEpochSecond());
		query.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}

}
