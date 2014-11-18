package se.solit.timeit.dao;

import java.sql.SQLException;
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

	public final void update(final Task paramTask) throws SQLException
	{
		EntityManager em = emf.createEntityManager();
		try
		{
			Task task = em.find(Task.class, paramTask.getID());
			em.getTransaction().begin();
			task.setName(paramTask.getName());
			task.setParent(paramTask.getParent());
			task.setCompleted(paramTask.getCompleted());
			task.setLastChange(paramTask.getLastChange());
			task.setCompleted(paramTask.getCompleted());
			task.setOwner(paramTask.getOwner());
			task.setDeleted(paramTask.getDeleted());
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
		TypedQuery<Task> getQuery = em.createQuery("SELECT t FROM Task t WHERE t.owner.username = :username",
				Task.class);
		getQuery.setParameter("username", username);
		List<Task> tasks = getQuery.getResultList();
		em.close();
		return tasks;
	}

	public final void updateOrAdd(final Task[] taskArray) throws SQLException
	{
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
				add(task);
			}
		}
	}

	public Task getTask(String id)
	{
		return getByID(id);
	}

}
