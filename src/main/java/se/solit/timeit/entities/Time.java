package se.solit.timeit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;

import se.solit.timeit.serializers.DateAsTimestampDeserializer;
import se.solit.timeit.serializers.DateAsTimestampSerializer;
import se.solit.timeit.serializers.TaskSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class Time
{

	@Id
	@Column(nullable = false)
	private String		id;

	@ManyToOne(targetEntity = Task.class)
	@JoinColumn(name = "task", nullable = false)
	@JsonSerialize(using = TaskSerializer.class)
	private Task		task;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	@JsonDeserialize(using = DateAsTimestampDeserializer.class)
	private DateTime	start;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	@JsonDeserialize(using = DateAsTimestampDeserializer.class)
	private DateTime	stop;

	private boolean		deleted;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	@JsonDeserialize(using = DateAsTimestampDeserializer.class)
	private DateTime	changed;

	protected Time()
	{
	}

	public Time(final String paramUuid, final DateTime paramStart, final DateTime paramStop,
			final boolean paramDeleted, final DateTime paramChanged, final Task paramTask)
	{
		id = paramUuid;
		start = paramStart;
		stop = paramStop;
		deleted = paramDeleted;
		task = paramTask;
		changed = paramChanged;
	}

	public final DateTime getChanged()
	{
		return changed;
	}

	public final void setChanged(final DateTime changed2)
	{
		this.changed = changed2;
	}

	public final void setTask(final Task task2)
	{
		changed = DateTime.now();
		this.task = task2;
	}

	public final void setStart(final DateTime start2)
	{
		changed = DateTime.now();
		this.start = start2;
	}

	public final void setStop(final DateTime stop2)
	{
		changed = DateTime.now();
		this.stop = stop2;
	}

	public final void setDeleted(final boolean deleted2)
	{
		changed = DateTime.now();
		this.deleted = deleted2;
	}

	public final String getID()
	{
		return id;
	}

	public final DateTime getStart()
	{
		return start;
	}

	public final DateTime getStop()
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
		result = prime * result + changed.hashCode();
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + start.hashCode();
		result = prime * result + stop.hashCode();
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
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
		{
			return false;
		}
		if (!start.equals(other.start))
		{
			return false;
		}
		if (!stop.equals(other.stop))
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
