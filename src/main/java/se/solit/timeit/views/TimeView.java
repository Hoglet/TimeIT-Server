package se.solit.timeit.views;

import java.sql.SQLException;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TaskDescriptor;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class TimeView extends BaseView
{
	private final Time		time;
	private final TaskDAO	taskDAO;

	public TimeView(EntityManagerFactory emf, Time time, User user, HttpContext context, HttpSession session)
	{
		super("time.ftl", user, context, session);
		this.time = time;
		taskDAO = new TaskDAO(emf);
	}

	public Time getTime()
	{
		return time;
	}

	public TaskDescriptorList getTasks() throws SQLException
	{
		TaskDescriptorList list = new TaskDescriptorList();
		Collection<Task> tasks = taskDAO.getTasks(user.getUsername());
		for (Task t : tasks)
		{
			list.add(new TaskDescriptor(t));
		}
		return list;
	}

}
