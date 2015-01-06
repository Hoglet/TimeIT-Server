package se.solit.timeit.dao;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

public class TimeDAO
{
	private static final int			MILLISECONDS_PER_SECOND	= 1000;
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

	public Time getByID(final UUID id) throws SQLException
	{
		Time item = null;
		EntityManager em = entityManagerFactory.createEntityManager();
		item = em.find(Time.class, id);
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

	public List<Entry<Task, Duration>> getTimesSummary(User user, DateTime start, DateTime stop)
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		List<Entry<Task, Duration>> returnValue = new ArrayList<Map.Entry<Task, Duration>>();
		List<Object[]> results = getCompletelyWithin(user, start, stop, em);
		addToResult(returnValue, results);
		results = getStartingBefore(user, start, stop, em);
		addToResult(returnValue, results);
		results = getEndingAfter(user, start, stop, em);
		addToResult(returnValue, results);
		em.close();
		return returnValue;
	}

	private List<Object[]> getEndingAfter(User user, DateTime start, DateTime stop, EntityManager em)
	{
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(:stop-t.start) FROM Time t WHERE t.task.owner = :user AND t.start>=:start AND t.start<=:stop AND t.stop>:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter("user", user);
		getTimesQuery.setParameter("start", start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter("stop", stop.getMillis() / MILLISECONDS_PER_SECOND);
		return getTimesQuery.getResultList();
	}

	private List<Object[]> getStartingBefore(User user, DateTime start, DateTime stop, EntityManager em)
	{
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(t.stop-:start) FROM Time t WHERE t.task.owner = :user AND t.start<:start AND t.stop>=:start AND t.stop<=:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter("user", user);
		getTimesQuery.setParameter("start", start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter("stop", stop.getMillis() / MILLISECONDS_PER_SECOND);
		return getTimesQuery.getResultList();
	}

	private List<Object[]> getCompletelyWithin(User user, DateTime start, DateTime stop, EntityManager em)
	{
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(t.stop-t.start) FROM Time t WHERE t.task.owner = :user AND t.start>=:start AND t.stop<=:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter("user", user);
		getTimesQuery.setParameter("start", start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter("stop", stop.getMillis() / MILLISECONDS_PER_SECOND);

		return getTimesQuery.getResultList();
	}

	private void addToResult(List<Entry<Task, Duration>> returnValue, List<Object[]> results)
	{
		for (Object[] result : results)
		{
			Task task = (Task) result[0];
			long millis = MILLISECONDS_PER_SECOND * (long) result[1];
			Duration duration = Duration.millis(millis);
			Entry<Task, Duration> existingEntry = findInList(returnValue, task);
			if (existingEntry != null)
			{
				Duration newDuration = existingEntry.getValue().plus(duration);
				existingEntry.setValue(newDuration);
			}
			else
			{
				Entry<Task, Duration> entry = new SimpleEntry<Task, Duration>(task, duration);
				returnValue.add(entry);
			}
		}
	}

	private Entry<Task, Duration> findInList(List<Entry<Task, Duration>> returnValue, Task task)
	{
		UUID taskId = task.getID();
		for (Entry<Task, Duration> entry : returnValue)
		{
			if (entry.getKey().getID().equals(taskId))
			{
				return entry;
			}
		}
		return null;
	}
}
