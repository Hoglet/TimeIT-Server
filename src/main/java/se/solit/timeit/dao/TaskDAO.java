package se.solit.timeit.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;

import se.solit.timeit.entities.Task;

public class TaskDAO
{
	private static final long			MILLISECONDS_PER_SECOND	= 1000;
	private final EntityManagerFactory	emf;

	public TaskDAO(final EntityManagerFactory entityManagerFactory)
	{
		emf = entityManagerFactory;
	}

	public final void update(final Task task) throws SQLException
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			final Task existingTask = getByID(task.getID());
			manageRecursion(existingTask, task, em);
			em.getTransaction().begin();
			if (isChangedAfterExisting(task, existingTask))
			{
				em.merge(task);
			}
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}

	}

	private void manageRecursion(Task existingTask, Task task, EntityManager em) throws SQLException
	{
		Task junction = findChildParent(existingTask, task);
		if (junction != null)
		{
			junction.setParent(existingTask.getParent());
			em.merge(junction);
		}
	}

	private Task findChildParent(Task existingTask, Task task)
	{
		Task parent = task.getParent();
		if (parent != null)
		{
			if (existingTask.getID().equals(parent.getID()))
			{
				return task;
			}
			else
			{
				return findChildParent(existingTask, parent);
			}
		}
		return parent;
	}

	public final void add(final Task task)
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			em.getTransaction().begin();
			em.persist(task);
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}
	}

	public Task getByID(String id)
	{
		Task task = null;
		EntityManager em = emf.createEntityManager();
		task = em.find(Task.class, id);
		em.close();
		return task;
	}

	public final Task getByID(final UUID id)
	{
		return getByID(id.toString());
	}

	public Collection<Task> getAllTasks(String username)
	{
		return getAllTasks(username, new DateTime(0));
	}

	public Collection<Task> getAllTasks(String username, DateTime since)
	{
		EntityManager em = emf.createEntityManager();
		long pointInTime = since.getMillis() / MILLISECONDS_PER_SECOND;
		List<Task> tasks = iGetTasks(username, em, false, pointInTime);
		tasks.addAll(iGetTasks(username, em, true, pointInTime));
		em.close();
		return tasks;
	}

	public final List<Task> getTasks(final String username) throws SQLException
	{
		EntityManager em = emf.createEntityManager();
		List<Task> tasks = iGetTasks(username, em, false, 0);
		em.close();
		return tasks;
	}

	static List<Task> iGetTasks(final String username, EntityManager em, boolean deleted, long since)
	{
		TypedQuery<Task> getQuery = em
				.createQuery(
						"SELECT t FROM Task t WHERE t.owner.username = :username AND t.deleted = :deleted AND t.lastChange>=:change",
						Task.class);
		getQuery.setParameter("username", username);
		getQuery.setParameter("deleted", deleted);
		getQuery.setParameter("change", since);
		return getQuery.getResultList();
	}

	public final void updateOrAdd(final Task[] taskArray) throws SQLException
	{

		if (taskArray.length > 0)
		{

			Collection<Task> unAddedTasks = new ArrayList<Task>();
			for (final Task task : taskArray)
			{
				final Task existingTask = getByID(task.getID());
				if (hasUnknownParent(task))
				{
					unAddedTasks.add(task);
				}
				else if (existingTask != null)
				{
					update(task);
				}
				else
				{
					add(task);
				}
			}
			if (taskArray.length != unAddedTasks.size())
			{
				updateOrAdd(unAddedTasks.toArray(new Task[unAddedTasks.size()]));
			}
		}
	}

	private boolean hasUnknownParent(final Task task)
	{
		return task.getParent() != null && getByID(task.getParent().getID()) == null;
	}

	private boolean isChangedAfterExisting(final Task task, final Task existingTask)
	{
		return !existingTask.equals(task) && (task.getLastChange().isAfter(existingTask.getLastChange()));
	}

	public List<Task> getTasks(String username, Task parent, boolean includeDeleted)
	{
		EntityManager em = emf.createEntityManager();
		TypedQuery<Task> getQuery;
		if (parent == null)
		{
			getQuery = em
					.createQuery(
							"SELECT t FROM Task t WHERE t.owner.username = :username AND t.deleted = :deleted AND t.parent IS NULL",
							Task.class);
		}
		else
		{
			getQuery = em
					.createQuery(
							"SELECT t FROM Task t WHERE t.owner.username = :username  AND t.deleted = :deleted AND t.parent = :parent",
							Task.class);
			getQuery.setParameter("parent", parent);
		}
		getQuery.setParameter("username", username);
		getQuery.setParameter("deleted", includeDeleted);
		return new ArrayList<Task>(getQuery.getResultList());
	}

	public void delete(Task task2)
	{
		EntityManager em = emf.createEntityManager();
		task2.setDeleted(true);
		try
		{
			em.getTransaction().begin();
			em.merge(task2);
			List<Task> tasks = getNotDeletedChildren(task2, em);
			for (Task task : tasks)
			{
				task.setParent(null);
			}
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}

	}

	private List<Task> getNotDeletedChildren(Task task2, EntityManager em)
	{
		TypedQuery<Task> getQuery = em.createQuery(
				"SELECT t FROM Task t WHERE t.parent = :parent AND t.deleted = false", Task.class);
		getQuery.setParameter("parent", task2);
		return getQuery.getResultList();
	}

}
