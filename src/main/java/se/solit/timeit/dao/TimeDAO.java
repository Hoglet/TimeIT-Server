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
			Time item = em.find(Time.class, paramTimeItem.getUUID());
			em.getTransaction().begin();
			item.setTask(paramTimeItem.getTask());
			item.setStart(paramTimeItem.getStart());
			item.setStop(paramTimeItem.getStop());
			item.setDeleted(paramTimeItem.getDeleted());
			item.setChanged(paramTimeItem.getChanged());
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
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			em.close();
		}
	}

	private Time getByID(final String uuid) throws SQLException
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
		TypedQuery<Time> getTimesQuery = em.createQuery(
				"SELECT t FROM TimeItem t WHERE t.task.owner.username = :username", Time.class);
		getTimesQuery.setParameter("username", username);
		List<Time> items = getTimesQuery.getResultList();
		em.close();
		return items;
	}

	public final void updateOrAdd(final Time[] itemArray) throws SQLException
	{
		for (final Time item : itemArray)
		{
			final Time existingItem = getByID(item.getUUID());
			if (existingItem != null)
			{
				if (!existingItem.equals(item) && (item.getChanged() >= (existingItem.getChanged())))
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
