package se.solit.timeit.views;

import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TaskDescriptor;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TaskView extends BaseView
{

	private final Task		task;
	private final TaskDAO	taskdao;
	private final Action	action;

	public TaskView(EntityManagerFactory emf, Task task, User user, Action action, UriInfo uriInfo,
					HttpSession session)
	{
		super("task.ftl", user, uriInfo, session);
		this.task = task;
		this.action = action;
		taskdao = new TaskDAO(emf);
	}

	public Task getTask()
	{
		return task;
	}

	public TaskDescriptorList getParents() throws SQLException
	{
		TaskDescriptorList list = new TaskDescriptorList();
		Collection<Task> tasks = taskdao.getTasks(user.getUsername());
		for (Task t : tasks)
		{
			if (t.equals(task) == false)
			{
				list.add(new TaskDescriptor(t));
			}
		}
		return list;
	}

	public boolean edit()
	{
		return action == Action.EDIT;
	}

}
