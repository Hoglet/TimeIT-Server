package se.solit.timeit.dao;

import java.util.ArrayList;
import java.util.UUID;

import org.joda.time.Duration;

import se.solit.timeit.entities.Task;

public class TimeDescriptorList extends ArrayList<TimeDescriptor>
{
	private static final long	serialVersionUID	= 1L;

	public TimeDescriptor find(Task task)
	{
		UUID taskId = task.getID();
		for (TimeDescriptor item : this)
		{
			if (item.getTask().getID().equals(taskId))
			{
				return item;
			}
		}
		return null;
	}

	@Override
	public boolean add(TimeDescriptor td)
	{
		Task task = td.getTask();
		for (TimeDescriptor item : this)
		{
			if (item.getTask().equals(task))
			{
				Duration newDuration = item.getDuration().plus(td.getDuration());
				item.setDuration(newDuration);

				Duration newDurationWithChildren = item.getDurationWithChildren().plus(td.getDurationWithChildren());
				item.setDurationWithChildren(newDurationWithChildren);
				return true;
			}
		}
		Task parent = task.getParent();
		if (parent != null)
		{
			int index = this.indexOf(find(parent));
			super.add(index + 1, td);
		}
		else
		{
			super.add(td);
		}
		return false;
	}
}
