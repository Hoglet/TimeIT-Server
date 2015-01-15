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
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class TimeView extends BaseView
{
	private final Time		time;
	private final TaskDAO	taskDAO;

	public TimeView(EntityManagerFactory emf, Time time, User user, HttpContext context)
	{
		super("time.ftl", user, context);
		this.time = time;
		taskDAO = new TaskDAO(emf);
	}

	public Time getTime()
	{
		return time;
	}

	public List<Entry<String, String>> getTasks() throws SQLException
	{
		List<Entry<String, String>> list = new ArrayList<Entry<String, String>>();
		Collection<Task> tasks = taskDAO.getTasks(user.getUsername());
		for (Task t : tasks)
		{
			String parentString = getParentString(t);
			list.add(new SimpleEntry<String, String>(t.getID().toString(), parentString));
		}
		return list;
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
