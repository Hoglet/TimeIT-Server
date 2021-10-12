package se.solit.timeit.dao;

import java.time.Duration;

import org.apache.commons.lang3.StringUtils;

import se.solit.timeit.entities.Task;

public class TimeDescriptor
{

	private static final int    ALMOSTMINUTE = 59999;
	private Duration            duration;
	private final Task          task;
	private Duration            durationWithChildren;
	private final int           numberOfAncestors;
	private final String        indentString;

	public TimeDescriptor(Task task, Duration duration, Duration durationWithChildren)
	{
		this.task = task;
		this.duration = duration;
		this.durationWithChildren = durationWithChildren;

		this.numberOfAncestors = calculateNumberOfAncestors(task);
		this.indentString = StringUtils.repeat("&nbsp;&nbsp;&nbsp;", numberOfAncestors);
	}

	private int calculateNumberOfAncestors(Task t)
	{
		Task parent = t.getParent();
		if (parent != null)
		{
			return 1 + calculateNumberOfAncestors(parent);
		}
		return 0;
	}

	public String getIndentString()
	{
		return indentString;
	}

	public Duration getDuration()
	{
		return duration;
	}

	public String getDurationString()
	{
		String result = "";
		if (duration.compareTo(Duration.ofMillis(ALMOSTMINUTE)) > 0)
		{
			long hours = duration.getSeconds() / 3600;
			long minutes = duration.minusHours(hours).getSeconds() / 60;

			result = String.format("%02d:%02d", hours, minutes);
		}
		return result;
	}

	public void setDuration(Duration newDuration)
	{
		duration = newDuration;
	}

	public Duration getDurationWithChildren()
	{
		return durationWithChildren;
	}

	public String getDurationWithChildrenStringAlways()
	{
		long hours = durationWithChildren.getSeconds() / 3600;
		long minutes = durationWithChildren.minusHours(hours).getSeconds() / 60;

		return String.format("%02d:%02d", hours, minutes);
	}

	public String getDurationWithChildrenString()
	{
		String result = "";
		if (!durationWithChildren.equals(duration))
		{
			result = getDurationWithChildrenStringAlways();
		}
		return result;
	}

	public void setDurationWithChildren(Duration newDurationWithChildren)
	{
		this.durationWithChildren = newDurationWithChildren;
	}

	public Task getTask()
	{
		return task;
	}

}
