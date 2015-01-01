package se.solit.timeit.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import se.solit.timeit.entities.Time;

public class TimeDAO
{
	private final EntityManagerFactory	entityManagerFactory;

	public TimeDAO(final EntityManagerFactory emf)
	{
		entityManagerFactory = emf;
	}

	public final void update(final Time paramTimeItem) throws SQLException
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		try
		{
			em.getTransaction().begin();
			em.merge(paramTimeItem);
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}
	}

	public final void add(final Time paramTimeItem) throws SQLException
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		try
		{
			em.getTransaction().begin();
			em.persist(paramTimeItem);
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}
	}

	public Time getByID(final String uuid) throws SQLException
	{
		Time item = null;
		EntityManager em = entityManagerFactory.createEntityManager();
		item = em.find(Time.class, uuid);
		em.close();
		return item;
	}

	public final Collection<Time> getTimes(final String username) throws SQLException
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		List<Time> items = iGetTimes(username, em);
		em.close();
		return items;
	}

	static List<Time> iGetTimes(final String username, EntityManager em)
	{
		TypedQuery<Time> getTimesQuery = em.createQuery("SELECT t FROM Time t WHERE t.task.owner.username = :username",
				Time.class);
		getTimesQuery.setParameter("username", username);
		return getTimesQuery.getResultList();
	}

	public final void updateOrAdd(final Time[] itemArray) throws SQLException
	{
		for (final Time item : itemArray)
		{
			final Time existingItem = getByID(item.getID());
			if (existingItem != null)
			{
				if (!existingItem.equals(item) && (item.getChanged().isAfter(existingItem.getChanged())))
				{
					update(item);
				}
			}
			else
			{
				add(item);
			}
		}
	}
}
