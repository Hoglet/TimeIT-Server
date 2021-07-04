package se.solit.timeit.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Column;
//import javax.persistence.Convert;
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
@Immutable
@JsonDeserialize(using = TimeDeserializer.class)
public class Time
{
	@Id
	@Column(nullable = false)
	private final String        id;

	@ManyToOne(targetEntity = Task.class)
	@JoinColumn(name = "task", nullable = false)
	@JsonSerialize(using = TaskSerializer.class)
	private final Task          task;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	//@Convert (converter = InstantConverter.class)
	private final long          start;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	//@Convert (converter = InstantConverter.class)
	private final long          stop;

	private final boolean		deleted;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	//@Convert (converter = InstantConverter.class)
	private final long         changed;

	@Column(nullable = true)
	private final String       comment;



	protected Time()
	{
		id      = "";
		changed = 0;
		start   = 0;
		stop    = 0;
		deleted = false;
		task    = new Task();
		comment = "";
	}

	public Time(final UUID paramUuid, final Instant paramStart, final Instant paramStop, final boolean paramDeleted,
			final Instant paramChanged, final Task paramTask, final String paramComment)
	{
		id      = paramUuid.toString();
		start   = paramStart.getEpochSecond();
		stop    = paramStop.getEpochSecond();
		deleted = paramDeleted;
		task    = paramTask;
		changed = paramChanged.getEpochSecond();
		comment = paramComment;
	}

	public final Instant getChanged()
	{
		return Instant.ofEpochSecond(changed);
	}

	public final UUID getID()
	{
		return UUID.fromString(id);
	}

	public final Instant getStart()
	{
		return Instant.ofEpochSecond(start);
	}

	public final Instant getStop()
	{
		return Instant.ofEpochSecond(stop);
	}

	public final boolean getDeleted()
	{
		return deleted;
	}

	public final Task getTask()
	{
		return task;
	}

	public final String getComment()
	{
		if(comment!=null)
		{
			return comment;
		}
		return "";
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
		result = prime * result + comment.hashCode();
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
		if( !Objects.equals(comment, other.comment))
		{
			return false;
		}
		return true;
	}

	public Time withStart(Instant start2)
	{
		UUID uuid = UUID.fromString(id);
		Instant stop2 = Instant.ofEpochSecond(stop);
		Instant changed2 = Instant.ofEpochSecond(changed);
		return new Time( uuid, start2, stop2, deleted, changed2, task, comment);
	}

	public Time withStop(Instant stop2)
	{
		UUID uuid = UUID.fromString(id);
		Instant start2 = Instant.ofEpochSecond(start);
		Instant changed2 = Instant.ofEpochSecond(changed);
		return new Time( uuid, start2, stop2, deleted, changed2, task, comment);
	}

	// SONAR:ON
	// CHECKSTYLE:ON
}
