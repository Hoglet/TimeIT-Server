package se.solit.timeit.dao;

import java.sql.SQLException;
import java.util.List;
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
	private static final String			STOP					= "stop";
	private static final String			USER					= "user";
	private static final String			START					= "start";
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

	public final List<Time> getTimes(final String username) throws SQLException
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

	public TimeDescriptorList getTimes(User user, DateTime start, DateTime stop)
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		TimeDescriptorList returnValue = new TimeDescriptorList();
		List<Object[]> results = getCompletelyWithin(user, start, stop, em);
		addToResult2(returnValue, results);
		results = getStartingBefore(user, start, stop, em);
		addToResult2(returnValue, results);
		results = getEndingAfter(user, start, stop, em);
		addToResult2(returnValue, results);
		em.close();
		return returnValue;
	}

	private List<Object[]> getEndingAfter(User user, DateTime start, DateTime stop, EntityManager em)
	{
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(:stop-t.start) FROM Time t WHERE t.task.owner = :user AND t.start>=:start AND t.start<=:stop AND t.stop>:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter(USER, user);
		getTimesQuery.setParameter(START, start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter(STOP, stop.getMillis() / MILLISECONDS_PER_SECOND);
		return getTimesQuery.getResultList();
	}

	private List<Object[]> getStartingBefore(User user, DateTime start, DateTime stop, EntityManager em)
	{
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(t.stop-:start) FROM Time t WHERE t.task.owner = :user AND t.start<:start AND t.stop>=:start AND t.stop<=:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter(USER, user);
		getTimesQuery.setParameter(START, start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter(STOP, stop.getMillis() / MILLISECONDS_PER_SECOND);
		return getTimesQuery.getResultList();
	}

	private List<Object[]> getCompletelyWithin(User user, DateTime start, DateTime stop, EntityManager em)
	{
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(t.stop-t.start) FROM Time t WHERE t.task.owner = :user AND t.start>=:start AND t.stop<=:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter(USER, user);
		getTimesQuery.setParameter(START, start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter(STOP, stop.getMillis() / MILLISECONDS_PER_SECOND);

		return getTimesQuery.getResultList();
	}

	private void addToResult2(TimeDescriptorList returnValue, List<Object[]> results)
	{
		for (Object[] result : results)
		{
			Task task = (Task) result[0];
			long millis = MILLISECONDS_PER_SECOND * (long) result[1];
			Duration duration = Duration.millis(millis);
			Duration durationWithChildren = duration;
			addToResultList(returnValue, task, duration, durationWithChildren);
		}

	}

	private void addToResultList(TimeDescriptorList returnValue, Task task, Duration duration,
			Duration durationWithChildren)
	{
		Task parent = task.getParent();
		if (parent != null)
		{
			Duration zeroDuration = new Duration(0);
			addToResultList(returnValue, parent, zeroDuration, durationWithChildren);
		}
		TimeDescriptor existingItem = returnValue.find(task);
		if (existingItem != null)
		{
			Duration newDuration = existingItem.getDuration().plus(duration);
			existingItem.setDuration(newDuration);
			Duration newDurationWithChildren = existingItem.getDurationWithChildren().plus(durationWithChildren);
			existingItem.setDurationWithChildren(newDurationWithChildren);

		}
		else
		{
			TimeDescriptor entry = new TimeDescriptor(task, duration, durationWithChildren);
			returnValue.add(entry);
		}
	}

	public List<Time> getTimeItems(Task task, DateTime start, DateTime stop)
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		TypedQuery<Time> getTimesQuery = em
				.createQuery(
						"SELECT t FROM Time t WHERE t.task=:task AND ((t.start>=:start AND t.start<=:stop) OR (t.stop>=:start AND t.stop<=:stop))",
						Time.class);
		getTimesQuery.setParameter("task", task);
		getTimesQuery.setParameter(START, start.getMillis() / MILLISECONDS_PER_SECOND);
		getTimesQuery.setParameter(STOP, stop.getMillis() / MILLISECONDS_PER_SECOND);
		return getTimesQuery.getResultList();
	}
}
