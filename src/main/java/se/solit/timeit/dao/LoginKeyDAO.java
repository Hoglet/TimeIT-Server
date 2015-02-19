package se.solit.timeit.dao;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import se.solit.timeit.entities.LoginKey;

public class LoginKeyDAO
{

	private static final long			MILLIS_PER_SECOND	= 1000;
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
		DateTime pointInTime = DateTime.now().minus(duration);
		query.setParameter("point", pointInTime.getMillis() / MILLIS_PER_SECOND);
		query.executeUpdate();
		em.getTransaction().commit();
		em.close();
	}

}
