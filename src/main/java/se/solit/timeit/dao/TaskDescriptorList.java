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
			throw new IllegalArgumentException("Task allready exists");
		}
		Task parent = td.getTask().getParent();
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
