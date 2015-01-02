package se.solit.timeit.views;

import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class TaskView extends BaseView
{

	private final Task		task;
	private final TaskDAO	taskdao;
	private final Action	action;

	public TaskView(EntityManagerFactory emf, Task task, User user, Action action)
	{
		super("task.ftl", user);
		this.task = task;
		this.action = action;
		taskdao = new TaskDAO(emf);
	}

	public Task getTask()
	{
		return task;
	}

	public List<Entry<String, String>> getParents() throws SQLException
	{
		List<Entry<String, String>> list = new ArrayList<Entry<String, String>>();
		Collection<Task> tasks = taskdao.getTasks(user.getUsername());
		for (Task t : tasks)
		{
			String parentString = getParentString(t);
			list.add(new SimpleEntry<String, String>(t.getID().toString(), parentString));
		}
		return list;
	}

	public boolean edit()
	{
		return action == Action.EDIT;
	}

	private String getParentString(Task task2)
	{
		String result = "";
		if (task2.getParent() != null)
		{
			result = getParentString(task2.getParent()).concat("/");
		}
		return result.concat(task2.getName());
	}

}
