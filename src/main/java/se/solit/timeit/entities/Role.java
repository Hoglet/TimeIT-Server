package se.solit.timeit.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "role")
public class Role
{
	public static final String	ADMIN	= "Admin";

	@Transient
	private boolean				checked;

	@Id
	private String				name;

	protected Role()
	{
	}

	public Role(final String string)
	{
		name = string;
		checked = false;
	}

	public final String getName()
	{
		return name;
	}

	public final void setName(final String role2)
	{
		name = role2;
	}

	@Transient
	public final void setCheckedState(boolean state)
	{
		checked = state;
	}

	@Transient
	public final boolean getCheckedState()
	{
		return checked;
	}

	@Override
	public boolean equals(Object other)
	{
		boolean result = false;
		if (other instanceof Role)
		{
			Role otherRole = (Role)other;
			result = name.equals(otherRole.getName());
		}
		return result;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

}
