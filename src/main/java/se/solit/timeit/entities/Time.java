package se.solit.timeit.entities;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

import se.solit.timeit.serializers.DateAsTimestampSerializer;
import se.solit.timeit.serializers.TaskSerializer;
import se.solit.timeit.serializers.TimeDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@JsonDeserialize(using = TimeDeserializer.class)
public class Time
{

	private static final int	MILLISECONDS_PER_SECOND	= 1000;

	@Id
	@Column(nullable = false)
	private String				id;

	@ManyToOne(targetEntity = Task.class)
	@JoinColumn(name = "task", nullable = false)
	@JsonSerialize(using = TaskSerializer.class)
	private Task				task;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	private long				start;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	private long				stop;

	private boolean				deleted;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	private DateTime			changed;

	protected Time()
	{
	}

	public Time(final UUID paramUuid, final DateTime paramStart, final DateTime paramStop, final boolean paramDeleted,
			final DateTime paramChanged, final Task paramTask)
	{
		id = paramUuid.toString();
		start = paramStart.getMillis() / MILLISECONDS_PER_SECOND;
		stop = paramStop.getMillis() / MILLISECONDS_PER_SECOND;
		deleted = paramDeleted;
		task = paramTask;
		changed = paramChanged;
	}

	public final DateTime getChanged()
	{
		return changed;
	}

	public final void setTask(final Task task2)
	{
		changed = DateTime.now();
		this.task = task2;
	}

	public final void setStart(final DateTime start2)
	{
		changed = DateTime.now();
		this.start = start2.getMillis() / MILLISECONDS_PER_SECOND;
	}

	public final void setStop(final DateTime stop2)
	{
		changed = DateTime.now();
		this.stop = stop2.getMillis() / MILLISECONDS_PER_SECOND;
	}

	public final void setDeleted(final boolean deleted2)
	{
		changed = DateTime.now();
		this.deleted = deleted2;
	}

	public final UUID getID()
	{
		return UUID.fromString(id);
	}

	public final DateTime getStart()
	{
		return new DateTime(start * MILLISECONDS_PER_SECOND);
	}

	public final DateTime getStop()
	{
		return new DateTime(stop * MILLISECONDS_PER_SECOND);
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
		result = prime * result + changed.hashCode();
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + id.hashCode();
		result = prime * result + Long.valueOf(start).hashCode();
		result = prime * result + Long.valueOf(stop).hashCode();
		result = prime * result + task.getID().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		Time other = (Time) obj;
		long diff = Math.abs(changed.getMillis() - other.getChanged().getMillis());
		if (diff > 1000)
		{
			return false;
		}
		if (deleted != other.deleted)
		{
			return false;
		}
		if (!id.equals(other.id))
		{
			return false;
		}
		if (start != other.start)
		{
			return false;
		}
		if (stop != other.stop)
		{
			return false;
		}
		if (!task.getID().equals(other.task.getID()))
		{
			return false;
		}
		return true;
	}

	// SONAR:ON
	// CHECKSTYLE:ON
}
