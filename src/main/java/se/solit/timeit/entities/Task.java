package se.solit.timeit.entities;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

import se.solit.timeit.serializers.DateAsTimestampSerializer;
import se.solit.timeit.serializers.TaskDeserializer;
import se.solit.timeit.serializers.TaskSerializer;
import se.solit.timeit.serializers.UserSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@XmlRootElement(name = "Task")
@JsonDeserialize(using = TaskDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Immutable
public class Task
{
	@Id
	@Column(nullable = false)
	private final String      id;
	private final String      name;
	
	@JsonSerialize(using = TaskSerializer.class)
	private final Task        parent;

	@JsonSerialize(using = DateAsTimestampSerializer.class)
	private final long        lastChange;
	private final boolean     deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner", nullable = false)
	@JsonSerialize(using = UserSerializer.class)
	private final User        owner;

	protected Task()
	{
		this.id = "";
		this.name = "";
		this.lastChange = 0;
		this.owner = null;
		this.deleted = false;
		this.parent = null;
	}

	public Task(final UUID paramID, final String paramName, final Task paramParent, final User paramOwner)
	{
			this(paramID, paramName, paramParent, Instant.now(), false, paramOwner);
	}

	public Task(final UUID paramID, final String paramName, final Task paramParent,
	        final Instant paramLastChanged, final boolean paramDeleted, final User paramOwner)
	{
		if (paramID == null)
		{
			throw new NullPointerException("id is not allowed to be null");
		}
		id = paramID.toString();
		name = paramName;
		parent = paramParent;
		deleted = paramDeleted;
		if (paramOwner == null)
		{
			throw new NullPointerException("Owner is not allowed to be null");
		}
		owner = paramOwner;
		lastChange = paramLastChanged.getEpochSecond();
	}


	public final Task withDeleted(final boolean deleted2)
	{
		UUID id2 = UUID.fromString(id);
		return new Task(id2 , name, parent, Instant.now(), deleted2, owner);
	}

	public final UUID getID()
	{
		return UUID.fromString(id);
	}

	public final String getName()
	{
		return name;
	}

	public final Task getParent()
	{
		return parent;
	}

	public final Instant getLastChange()
	{
		return Instant.ofEpochSecond(lastChange);
	}

	public final boolean getDeleted()
	{
		return deleted;
	}

	// CHECKSTYLE:OFF
	// SONAR:OFF
	@Override
	public final boolean equals(final Object obj)
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
		Task other = (Task) obj;
		if (deleted != other.deleted)
		{
			return false;
		}
		if (!id.equals(other.id))
		{
			return false;
		}
		if (lastChange != other.lastChange)
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (owner.getUsername().equals(other.owner.getUsername()) == false)
		{
			return false;
		}
		if (parent == null)
		{
			if (other.parent != null)
			{
				return false;
			}
		}
		else if (!parent.equals(other.parent))
		{
			return false;
		}
		return true;
	}

	@Override
	public final int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + id.hashCode();
		result = prime * result + Long.valueOf(lastChange).hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + owner.getUsername().hashCode();
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	// SONAR:ON
	// CHECKSTYLE:ON


	public final Task withName(final String name2)
	{
		UUID id2 = UUID.fromString(id);
		return new Task(id2 , name2, parent, Instant.now(), deleted, owner);
	}

	public final Task withParent(final Task parent2)
	{
		UUID id2 = UUID.fromString(id);
		return new Task(id2 , name, parent2, Instant.now(), deleted, owner);
	}

	public final User getOwner()
	{
		return owner;
	}

	public final Task withOwner(final User owner2)
	{
		if (owner2 == null)
		{
			throw new NullPointerException("Owner is not allowed to be null");
		}
		UUID id2 = UUID.fromString(id);
		return new Task(id2 , name, parent, Instant.now(), deleted, owner2);
	}

}
