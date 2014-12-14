package se.solit.timeit.views;

import io.dropwizard.views.View;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.google.common.base.Charsets;

public class IndexView extends View
{
	private final User	user;
	private TaskDAO		taskdao;

	public IndexView(User user2, EntityManagerFactory emf)
	{
		super("index.ftl", Charsets.UTF_8);
		user = user2;
		taskdao = new TaskDAO(emf);
	}

	public User getCurrentUser()
	{
		return user;
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
