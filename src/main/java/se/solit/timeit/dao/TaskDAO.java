package se.solit.timeit.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import se.solit.timeit.entities.Task;

public class TaskDAO
{
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
			em.getTransaction().begin();
			em.merge(task);
			em.getTransaction().commit();
		}
		finally
		{
			em.close();
		}

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

	public final Task getByID(final String uuid)
	{
		Task task = null;
		EntityManager em = emf.createEntityManager();
		task = em.find(Task.class, uuid);
		em.close();
		return task;
	}

	public final Collection<Task> getTasks(final String username) throws SQLException
	{
		EntityManager em = emf.createEntityManager();
		List<Task> tasks = _getTasks(username, em);
		em.close();
		return tasks;
	}

	static List<Task> _getTasks(final String username, EntityManager em)
	{
		TypedQuery<Task> getQuery = em.createQuery("SELECT t FROM Task t WHERE t.owner.username = :username",
				Task.class);
		getQuery.setParameter("username", username);
		List<Task> tasks = getQuery.getResultList();
		return tasks;
	}

	public final void updateOrAdd(final Task[] taskArray) throws SQLException
	{
		Collection<Task> unAddedTasks = new ArrayList<Task>();
		for (final Task task : taskArray)
		{
			final Task existingTask = getByID(task.getID());
			if (existingTask != null)
			{
				if (!existingTask.equals(task) && (task.getLastChange() >= (existingTask.getLastChange())))
				{
					update(task);
				}
			}
			else
			{
				if (task.getParent() != null && getByID(task.getParent().getID()) == null)
				{
					unAddedTasks.add(task);
				}
				else
				{
					add(task);
				}
			}
		}
		if (unAddedTasks.size() > 0)
		{
			updateOrAdd(unAddedTasks.toArray(new Task[unAddedTasks.size()]));
		}
	}

	public Task getTask(String id)
	{
		return getByID(id);
	}

}
