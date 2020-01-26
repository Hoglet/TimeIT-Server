package se.solit.timeit.entities;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import se.solit.timeit.serializers.DateAsTimestampSerializer;
import se.solit.timeit.serializers.TaskSerializer;
import se.solit.timeit.serializers.TimeDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@JsonDeserialize(using = TimeDeserializer.class)
public class Time
{
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
	private long				changed;

	private ZoneId zone;

	protected Time()
	{
	}

	public Time(final UUID paramUuid, final ZonedDateTime paramStart, final ZonedDateTime paramStop, final boolean paramDeleted,
			final ZonedDateTime paramChanged, final Task paramTask)
	{
		id = paramUuid.toString();
		start = Instant.from(paramStart).getEpochSecond();
		stop = Instant.from(paramStop).getEpochSecond();
		deleted = paramDeleted;
		task = paramTask;
		changed = Instant.from(paramChanged).getEpochSecond();
		zone = ZonedDateTime.now().getZone();
	}

	public final ZonedDateTime getChanged()
	{
		return Instant.ofEpochSecond(changed).atZone(zone);
	}

	public final void setTask(final Task task2)
	{
		changed =  Instant.now().getEpochSecond();
		this.task = task2;
	}

	public final void setStart(final ZonedDateTime start2)
	{
		changed =  Instant.now().getEpochSecond();
		this.start = Instant.from(start2).getEpochSecond();
	}

	public final void setStop(final ZonedDateTime stop2)
	{
		changed = Instant.now().getEpochSecond();
		this.stop = Instant.from(stop2).getEpochSecond();
	}

	public final void setDeleted(final boolean deleted2)
	{
		changed = Instant.now().getEpochSecond();
		this.deleted = deleted2;
	}

	public final UUID getID()
	{
		return UUID.fromString(id);
	}

	public final ZonedDateTime getStart()
	{
		return Instant.ofEpochSecond(start).atZone(zone);
	}

	public final ZonedDateTime getStop()
	{
		return Instant.ofEpochSecond(stop).atZone(zone);
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
		result = prime * result + Long.valueOf(changed).hashCode();
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
		if (changed != other.changed)
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
