package se.solit.timeit.views;

import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TaskDescriptor;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class TaskView extends BaseView
{

	private final Task		task;
	private final TaskDAO	taskdao;
	private final Action	action;

	public TaskView(EntityManagerFactory emf, Task task, User user, Action action, HttpContext context,
			HttpSession session)
	{
		super("task.ftl", user, context, session);
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
