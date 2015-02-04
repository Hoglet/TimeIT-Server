package se.solit.timeit.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import se.solit.timeit.entities.Task;

public class TaskDescriptorList extends ArrayList<TaskDescriptor>
{
	private static final long	serialVersionUID	= 1L;

	public TaskDescriptorList(Collection<Task> allTasks)
	{
		for (Task task : allTasks)
		{
			add(new TaskDescriptor(task));
		}
	}

	public TaskDescriptorList()
	{
	}

	public TaskDescriptor find(Task task)
	{
		UUID taskId = task.getID();
		for (TaskDescriptor item : this)
		{
			if (item.getTask().getID().equals(taskId))
			{
				return item;
			}
		}
		return null;
	}

	@Override
	public boolean add(TaskDescriptor td)
	{
		if (find(td.getTask()) != null)
		{
			return true;
		}
		Task parent = td.getTask().getParent();
		if (parent != null)
		{
			TaskDescriptor found = find(parent);
			if (found == null)
			{
				add(new TaskDescriptor(parent));
				found = find(parent);
			}
			int index = this.indexOf(found);
			super.add(index + 1, td);
			return true;
		}
		else
		{
			return super.add(td);
		}
	}
}
