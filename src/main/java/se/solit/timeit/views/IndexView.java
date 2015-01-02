package se.solit.timeit.views;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

public class IndexView extends BaseView
{
	private final TaskDAO	taskdao;

	public IndexView(User user, EntityManagerFactory emf)
	{
		super("index.ftl", user);
		taskdao = new TaskDAO(emf);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Entry<Task, List>> getTasks(Task parent)
	{
		List<Entry<Task, List>> list = null;
		List<Task> tasks = taskdao.getTasks(user.getUsername(), parent, false);
		if (!tasks.isEmpty())
		{
			list = new ArrayList();
			for (Task task : tasks)
			{
				List<Entry<Task, List>> children = getTasks(task);
				Entry<Task, List> entry = new SimpleEntry<Task, List>(task, children);
				list.add(entry);
			}
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes" })
	public List<Entry<Task, List>> getTasks()
	{
		return getTasks(null);
	}
}
