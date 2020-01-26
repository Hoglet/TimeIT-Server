package se.solit.timeit.dao;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

public class TimeDAO
{
	private final EntityManagerFactory	entityManagerFactory;
	
	private static final String  STOP   = "stop";
	private static final String  USER   = "user";
	private static final String  START  = "start";
	private ZonedDateTime        epoch  = Instant.ofEpochSecond(0).atZone(ZoneId.of("UTC"));

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
		item = em.find(Time.class, id.toString());
		em.close();
		return item;
	}

	public final Collection<Time> getTimes(final String username)
	{
		return getTimes(username, epoch);
	}

	public Collection<Time> getTimes(String username, ZonedDateTime param_time)
	{
		EntityManager em = entityManagerFactory.createEntityManager();
		Instant time = Instant.from(param_time);
		List<Time> items = iGetTimes(username, em, time.getEpochSecond());
		em.close();
		return items;
	}

	static List<Time> iGetTimes(final String username, EntityManager em, long time)
	{
		TypedQuery<Time> getTimesQuery = em
				.createQuery(
						"SELECT t FROM Time t WHERE t.task.owner.username = :username AND t.changed>=:change",
						Time.class);
		getTimesQuery.setParameter("username", username);
		getTimesQuery.setParameter("change", time);
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

	public TimeDescriptorList getTimes(User user, ZonedDateTime start, ZonedDateTime stop)
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

	private List<Object[]> getEndingAfter(User user, ZonedDateTime param_start, ZonedDateTime param_stop, EntityManager em)
	{
		Instant start = Instant.from(param_start);
		Instant stop  = Instant.from(param_stop);
		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(:stop-t.start) FROM Time t WHERE t.deleted=false AND t.task.owner = :user AND t.start>=:start AND t.start<=:stop AND t.stop>:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter(USER, user);
		getTimesQuery.setParameter(START, start.getEpochSecond());
		getTimesQuery.setParameter(STOP, stop.getEpochSecond());
		return getTimesQuery.getResultList();
	}

	private List<Object[]> getStartingBefore(User user, ZonedDateTime param_start, ZonedDateTime param_stop, EntityManager em)
	{
		Instant start = Instant.from(param_start);
		Instant stop  = Instant.from(param_stop);

		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(t.stop-:start) FROM Time t WHERE t.deleted=false AND t.task.owner = :user AND t.start<:start AND t.stop>=:start AND t.stop<=:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter(USER, user);
		getTimesQuery.setParameter(START, start.getEpochSecond()) ;
		getTimesQuery.setParameter(STOP, stop.getEpochSecond());
		return getTimesQuery.getResultList();
	}

	private List<Object[]> getCompletelyWithin(User user, ZonedDateTime param_start, ZonedDateTime param_stop, EntityManager em)
	{
		Instant start = Instant.from(param_start);
		Instant stop  = Instant.from(param_stop);

		TypedQuery<Object[]> getTimesQuery = em
				.createQuery(
						"SELECT t.task, SUM(t.stop-t.start) FROM Time t WHERE t.deleted=false AND t.task.owner = :user AND t.start>=:start AND t.stop<=:stop GROUP BY t.task",
						Object[].class);
		getTimesQuery.setParameter(USER, user);
		getTimesQuery.setParameter(START, start.getEpochSecond());
		getTimesQuery.setParameter(STOP, stop.getEpochSecond());

		return getTimesQuery.getResultList();
	}

	private void addToResult2(TimeDescriptorList returnValue, List<Object[]> results)
	{
		for (Object[] result : results)
		{
			Task task = (Task) result[0];
			Duration duration = Duration.ofSeconds((long) result[1]);
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
			Duration zeroDuration = Duration.ofSeconds(0);
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

	public List<Time> getTimeItems(Task task, ZonedDateTime param_start, ZonedDateTime param_stop)
	{
		Instant start = Instant.from(param_start);
		Instant stop  = Instant.from(param_stop);
		
		EntityManager em = entityManagerFactory.createEntityManager();
		TypedQuery<Time> getTimesQuery = em
				.createQuery(
						"SELECT t FROM Time t WHERE t.deleted=false AND t.task=:task AND ((t.start>=:start AND t.start<=:stop) OR (t.stop>=:start AND t.stop<=:stop))",
						Time.class);
		getTimesQuery.setParameter("task", task);
		getTimesQuery.setParameter(START, start.getEpochSecond());
		getTimesQuery.setParameter(STOP, stop.getEpochSecond());
		return getTimesQuery.getResultList();
	}

}
