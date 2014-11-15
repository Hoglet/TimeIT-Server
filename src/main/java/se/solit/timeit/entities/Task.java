package se.solit.timeit.entities;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@XmlRootElement(name = "Task")
public class Task
{
	@Id
	@Column(nullable = false)
	private String				id;
	private String				name;
	private String				parent;
	private boolean				completed;
	private long				lastChange;
	private boolean				deleted;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "task")
	private Collection<Time>	times;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner", nullable = false)
	//@JsonSerialize(using = UserToIDSerializer.class)
	@JsonSerialize(using = UserSerializer.class)
	private User				owner;

	protected Task()
	{
	}

	public Task(final String paramID, final String paramName, final String paramParent, final boolean paramCompleted,
			final long paramLastChanged, final boolean paramDeleted, final User paramOwner)
	{
		init(paramID, paramName, paramParent, paramCompleted, paramLastChanged, paramDeleted, paramOwner);
	}

	private void
			init(final String paramID, final String paramName, final String paramParent, final boolean paramCompleted,
					final long paramLastChange, final boolean paramDeleted, final User paramOwner)
	{
		id = paramID;
		name = paramName;
		parent = paramParent;
		lastChange = paramLastChange;
		completed = paramCompleted;
		deleted = paramDeleted;
		owner = paramOwner;
	}

	public final void setLastChange(final long lastChange2)
	{
		this.lastChange = lastChange2;
	}

	public final void setDeleted(final boolean deleted2)
	{
		this.deleted = deleted2;
	}

	public final String getID()
	{
		return id;
	}

	public final String getName()
	{
		return name;
	}

	public final String getParent()
	{
		return parent;
	}

	public final long getLastChange()
	{
		return lastChange;
	}

	public final boolean getDeleted()
	{
		return deleted;
	}

	public final boolean getCompleted()
	{
		return completed;
	}

	// CHECKSTYLE:OFF
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
		Task other = (Task)obj;
		if (completed != other.completed)
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
		if (owner == null)
		{
			if (other.owner != null)
			{
				return false;
			}
		}
		else if (!owner.equals(other.owner))
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
		result = prime * result + (completed ? 1231 : 1237);
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (int)(lastChange ^ (lastChange >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		return result;
	}

	// CHECKSTYLE:ON

	public final void setName(final String name2)
	{
		name = name2;
	}

	public final void setParent(final String parent2)
	{
		parent = parent2;
	}

	public final void setCompleted(final boolean completed2)
	{
		completed = completed2;
	}

	public final User getOwner()
	{
		return owner;
	}

	public final void setOwner(final User owner2)
	{
		owner = owner2;
	}

}
