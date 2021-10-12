package se.solit.timeit.views;

import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.UriInfo;

import com.sun.net.httpserver.HttpContext;
import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TaskDescriptor;
import se.solit.timeit.dao.TaskDescriptorList;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;


public class TimeView extends BaseView
{
	private final Time               time;
	private final TaskDAO            taskDAO;

	private final DateTimeFormatter  dateFormatter  = DateTimeFormatter.ofPattern("yyy-MM-dd");
	private final DateTimeFormatter  timeFormatter  = DateTimeFormatter.ofPattern("HH:mm");
	private final ZoneId             zone           = ZonedDateTime.now().getZone();

	public TimeView(EntityManagerFactory emf, Time time, User user, UriInfo uriInfo, HttpSession session)
	{
		super("time.ftl", user, uriInfo, session);
		this.time = time;
		taskDAO = new TaskDAO(emf);
	}

	public Time getTime()
	{
		return time;
	}

	public String getStartDate()
	{
		return time.getStart().atZone(zone).format(dateFormatter);
	}

	public String getStartTime()
	{
		return time.getStart().atZone(zone).format(timeFormatter);
	}
	
	public String getStopTime()
	{
		return time.getStop().atZone(zone).format(timeFormatter);
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
