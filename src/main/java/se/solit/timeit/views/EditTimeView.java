package se.solit.timeit.views;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import se.solit.timeit.entities.Time;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class EditTimeView extends BaseView
{
	private final Time               time;

	private final DateTimeFormatter  dateFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
	private final DateTimeFormatter  timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

	public EditTimeView(EntityManagerFactory emf, Time time, User user, HttpContext context, HttpSession session)
	{
		super("editTime.ftl", user, context, session);
		this.time = time;
	}

	public Time getTime()
	{
		return time;
	}

	public String getStartDate()
	{
		return time.getStart().format(dateFormatter);
	}

	public String getStartTime()
	{
		return time.getStart().format(timeFormatter);
	}

	public String getStopTime()
	{
		return time.getStop().format(timeFormatter);
	}

	public String getTaskName() throws SQLException
	{
		return time.getTask().getName();
	}

}
