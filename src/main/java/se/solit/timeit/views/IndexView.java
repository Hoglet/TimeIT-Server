package se.solit.timeit.views;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class IndexView extends BaseView
{
	private final TaskDAO	taskdao;
	private final TimeDAO	timedao;

	public IndexView(User user, EntityManagerFactory emf, HttpContext context, HttpSession session)
	{
		super("index.ftl", user, context, session);
		taskdao = new TaskDAO(emf);
		timedao = new TimeDAO(emf);
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

	public TimeDescriptorList getTodaysTimes()
	{
		DateTime start = DateTime.now().withTimeAtStartOfDay();
		// SONAR:OFF
		DateTime stop = DateTime.now().withTime(23, 59, 59, 0);
		// SONAR:ON
		return rootItems(timedao.getTimes(user, start, stop));
	}

	public TimeDescriptorList getMonthsTimes()
	{
		DateTime start = DateTime.now().withDayOfMonth(1).withTimeAtStartOfDay();
		// SONAR:OFF
		DateTime stop = start.plusMonths(1).minusDays(1).withTime(23, 59, 59, 0);
		// SONAR:ON
		return rootItems(timedao.getTimes(user, start, stop));
	}

	public TimeDescriptorList getYearsTimes()
	{
		DateTime start = DateTime.now().withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		// SONAR:OFF
		DateTime stop = start.plusMonths(1).minusDays(1).withTime(23, 59, 59, 0);
		// SONAR:ON
		return rootItems(timedao.getTimes(user, start, stop));
	}

	private TimeDescriptorList rootItems(TimeDescriptorList times)
	{
		Iterator<TimeDescriptor> it = times.iterator();
		while (it.hasNext())
		{
			TimeDescriptor item = it.next();
			if (item.getTask().getParent() != null)
			{
				it.remove();
			}
		}
		return times;
	}

}
