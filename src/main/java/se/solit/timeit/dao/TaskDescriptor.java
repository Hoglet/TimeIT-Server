package se.solit.timeit.dao;

import org.apache.commons.lang.StringUtils;

import se.solit.timeit.entities.Task;

public class TaskDescriptor
{

	private final Task		task;
	private final String	indentString;

	public TaskDescriptor(Task task)
	{
		this.task = task;
		int numberOfAncestors = calculateNumberOfAncestors(task);
		this.indentString = StringUtils.repeat("&nbsp;&nbsp;&nbsp;", numberOfAncestors);
	}

	public Task getTask()
	{
		return task;
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

	public String getName()
	{
		return task.getName();
	}

	public String getId()
	{
		return task.getID().toString();
	}

	@Override
	public String toString()
	{
		return getId() + "=" + getName();
	}
}
