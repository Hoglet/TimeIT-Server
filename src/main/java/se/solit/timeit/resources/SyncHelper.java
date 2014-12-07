package se.solit.timeit.resources;

import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

class SyncHelper
{
	private TaskDAO	taskdao;

	public SyncHelper(EntityManagerFactory emf)
	{
		taskdao = new TaskDAO(emf);
	}

	public void verifyHasAccess(User authorizedUser, String username)
	{
		if (!authorizedUser.getUsername().equals(username))
		{
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
	}

	public void verifyTaskOwnership(User authorizedUser, Task[] paramTasks)
	{
		String allowedUser = authorizedUser.getUsername();
		for (Task task : paramTasks)
		{
			if (!task.getOwner().getUsername().equals(allowedUser))
			{
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			}
		}

	}

	public void verifyTimesOwnership(User authorizedUser, Time[] paramTimes)
	{
		String allowedUser = authorizedUser.getUsername();
		for (Time time : paramTimes)
		{
			String taskID = time.getTask().getID();
			Task task = taskdao.getByID(taskID);
			if (!task.getOwner().getUsername().equals(allowedUser))
			{
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			}
		}
	}
}
