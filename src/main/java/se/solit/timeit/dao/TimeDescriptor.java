package se.solit.timeit.dao;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import se.solit.timeit.entities.Task;

public class TimeDescriptor
{

	private static final int		ALMOSTMINUTE	= 59999;
	private static final int		TWO				= 2;
	private Duration				duration;
	private final Task				task;
	private Duration				durationWithChildren;
	private final PeriodFormatter	minutesAndSeconds;
	private final int				numberOfAncestors;
	private final String			indentString;

	public TimeDescriptor(Task task, Duration duration, Duration durationWithChildren)
	{
		this.task = task;
		this.duration = duration;
		this.durationWithChildren = durationWithChildren;
		minutesAndSeconds = new PeriodFormatterBuilder().minimumPrintedDigits(TWO).printZeroAlways()
				.appendHours().appendSeparator(":").appendMinutes().toFormatter();
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
		if (duration.isLongerThan(new Duration(ALMOSTMINUTE)))
		{
			result = minutesAndSeconds.print(duration.toPeriod());
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

	public String getDurationWithChildrenString()
	{
		String result = "";
		if (!durationWithChildren.equals(duration))
		{
			result = minutesAndSeconds.print(durationWithChildren.toPeriod());
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
