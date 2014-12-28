package se.solit.timeit.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	private String	id;

	@ManyToOne(targetEntity = Task.class)
	@JoinColumn(name = "task", nullable = false)
	@JsonSerialize(using = TaskSerializer.class)
	private Task	task;

	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = DateAsTimestampSerializer.class)
	@JsonDeserialize(using = DateAsTimestampDeserializer.class)
	private Date	start;

	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = DateAsTimestampSerializer.class)
	@JsonDeserialize(using = DateAsTimestampDeserializer.class)
	private Date	stop;

	private boolean	deleted;

	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = DateAsTimestampSerializer.class)
	@JsonDeserialize(using = DateAsTimestampDeserializer.class)
	private Date	changed;

	protected Time()
	{
	}

	public Time(final String paramUuid, final Date paramStart, final Date paramStop, final boolean paramDeleted,
			final Date paramChanged, final Task paramTask)
	{
		id = paramUuid;
		start = (Date) paramStart.clone();
		stop = (Date) paramStop.clone();
		deleted = paramDeleted;
		task = paramTask;
		changed = (Date) paramChanged.clone();
	}

	public final Date getChanged()
	{
		return (Date) changed.clone();
	}

	public final void setChanged(final Date changed2)
	{
		this.changed = new Date(changed2.getTime());
	}

	public final void setTask(final Task task2)
	{
		changed = new Date();
		this.task = task2;
	}

	public final void setStart(final Date start2)
	{
		changed = new Date();
		this.start = new Date(start2.getTime());
	}

	public final void setStop(final Date stop2)
	{
		changed = new Date();
		this.stop = new Date(stop2.getTime());
	}

	public final void setDeleted(final boolean deleted2)
	{
		changed = new Date();
		this.deleted = deleted2;
	}

	public final String getID()
	{
		return id;
	}

	public final Date getStart()
	{
		return (Date) start.clone();
	}

	public final Date getStop()
	{
		return (Date) stop.clone();
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
		long diff = Math.abs(changed.getTime() - other.getChanged().getTime());
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
