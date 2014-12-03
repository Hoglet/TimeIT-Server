package se.solit.timeit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import se.solit.timeit.serializers.TaskSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class Time
{

	@Id
	@Column(nullable = false)
	private String	id;

	@ManyToOne(targetEntity = Task.class)
	@JoinColumn(name = "task", nullable = false)
	@JsonSerialize(using = TaskSerializer.class)
	private Task	task;
	private long	start;
	private long	stop;
	private boolean	deleted;
	private long	changed;

	protected Time()
	{
	}

	public Time(final String paramUuid, final long paramStart, final long paramStop, final boolean paramDeleted,
			final long paramChanged, final Task paramTask)
	{
		id = paramUuid;
		start = paramStart;
		stop = paramStop;
		deleted = paramDeleted;
		changed = paramChanged;
		task = paramTask;
	}

	public final long getChanged()
	{
		return changed;
	}

	public final void setChanged(final long changed2)
	{
		this.changed = changed2;
	}

	public final void setTask(final Task task2)
	{
		this.task = task2;
	}

	public final void setStart(final long start2)
	{
		this.start = start2;
	}

	public final void setStop(final long stop2)
	{
		this.stop = stop2;
	}

	public final void setDeleted(final boolean deleted2)
	{
		this.deleted = deleted2;
	}

	public final String getID()
	{
		return id;
	}

	public final long getStart()
	{
		return start;
	}

	public final long getStop()
	{
		return stop;
	}

	public final boolean getDeleted()
	{
		return deleted;
	}

	public final Task getTask()
	{
		return task;
	}

	// CHECKSTYLE:OFF
	// SONAR:OFF

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (changed ^ (changed >>> 32));
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
		result = prime * result + (int) (stop ^ (stop >>> 32));
		result = prime * result + task.getID().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Time other = (Time) obj;
		if (changed != other.changed)
			return false;
		if (deleted != other.deleted)
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (start != other.start)
			return false;
		if (stop != other.stop)
			return false;
		if (!task.getID().equals(other.task.getID()))
			return false;
		return true;
	}

	// SONAR:ON
	// CHECKSTYLE:ON
}
