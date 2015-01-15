package se.solit.timeit.views;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import se.solit.timeit.dao.TaskDAO;
import se.solit.timeit.dao.TimeDAO;
import se.solit.timeit.entities.Task;
import se.solit.timeit.entities.User;

import com.sun.jersey.api.core.HttpContext;

public class IndexView extends BaseView
{
	private final TaskDAO	taskdao;
	private final TimeDAO	timedao;

	public IndexView(User user, EntityManagerFactory emf, HttpContext context)
	{
		super("index.ftl", user, context);
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

	public List<Entry<String, String>> getTodaysTimes()
	{
		DateTime start = DateTime.now().withTimeAtStartOfDay();
		// SONAR:OFF
		DateTime stop = DateTime.now().withTime(23, 59, 59, 0);
		// SONAR:ON
		return getTimes(start, stop);
	}

	public List<Entry<String, String>> getMonthsTimes()
	{
		DateTime start = DateTime.now().withDayOfMonth(1).withTimeAtStartOfDay();
		// SONAR:OFF
		DateTime stop = start.plusMonths(1).minusDays(1).withTime(23, 59, 59, 0);
		// SONAR:ON
		return getTimes(start, stop);
	}

	public List<Entry<String, String>> getYearsTimes()
	{
		DateTime start = DateTime.now().withMonthOfYear(1).withDayOfMonth(1).withTimeAtStartOfDay();
		// SONAR:OFF
		DateTime stop = start.plusMonths(1).minusDays(1).withTime(23, 59, 59, 0);
		// SONAR:ON
		return getTimes(start, stop);
	}

	private List<Entry<String, String>> getTimes(DateTime start, DateTime stop)
	{
		// SONAR:OFF
		PeriodFormatter minutesAndSeconds = new PeriodFormatterBuilder().minimumPrintedDigits(2).printZeroAlways()
				.appendHours().appendSeparator(":").appendMinutes().toFormatter();
		// SONAR:ON
		List<Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();

		List<Entry<Task, Duration>> result = timedao.getTimesSummary(user, start, stop);
		for (Entry<Task, Duration> entry : result)
		{
			String name = entry.getKey().getName().toString();

			Period period = entry.getValue().toPeriod();
			String duration = minutesAndSeconds.print(period);

			SimpleEntry<String, String> entry2 = new SimpleEntry<String, String>(name, duration);
			list.add(entry2);
		}
		return list;
	}
}
