package se.solit.timeit.views;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Iterator;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.dao.TimeDescriptor;
import se.solit.timeit.dao.TimeDescriptorList;
import se.solit.timeit.entities.User;

public class IndexView extends BaseView
{
	private final TaskDAO	taskdao;
	private final TimeDAO	timedao;

	public IndexView(User user, EntityManagerFactory emf, UriInfo uriInfo, HttpSession session)
	{
		super("index.ftl", user, uriInfo, session);
		taskdao = new TaskDAO(emf);
		timedao = new TimeDAO(emf);
	}

	public TaskDescriptorList getTasks() throws SQLException
	{
		return new TaskDescriptorList(taskdao.getTasks(user.getUsername()));
	}

	public TimeDescriptorList getTodaysTimes()
	{
		ZonedDateTime start = ZonedDateTime.now().with(LocalTime.MIN);
		// SONAR:OFF
		ZonedDateTime stop = ZonedDateTime.now().with(LocalTime.MAX);
		// SONAR:ON
		return rootItems(timedao.getTimes(user, start, stop));
	}

	public TimeDescriptorList getMonthsTimes()
	{
		ZonedDateTime start = ZonedDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
		// SONAR:OFF
		ZonedDateTime stop = start.plusMonths(1).minusDays(1).with(LocalTime.MAX);
		// SONAR:ON
		return rootItems(timedao.getTimes(user, start, stop));
	}

	public TimeDescriptorList getYearsTimes()
	{
		ZonedDateTime start = ZonedDateTime.now().withMonth(1).withDayOfMonth(1).with(LocalTime.MIN);
		// SONAR:OFF
		ZonedDateTime stop = start.withMonth(12).withDayOfMonth(31).with(LocalTime.MAX);
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
