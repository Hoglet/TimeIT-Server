package se.solit.timeit.views;

import java.sql.SQLException;
import java.util.Iterator;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
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

	public TaskDescriptorList getTasks() throws SQLException
	{
		return new TaskDescriptorList(taskdao.getTasks(user.getUsername()));
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
