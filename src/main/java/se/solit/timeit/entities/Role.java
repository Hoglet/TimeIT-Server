package se.solit.timeit.entities;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Immutable
@Table(name = "role")
public class Role
{
	public static final String	ADMIN	= "Admin";

	@Transient
	private final boolean  checked;

	@Id
	private final String   name;

	protected Role()
	{
		this(null);
	}

	public Role(final String op_name)
	{
		this(op_name, false);
	}
	
	private Role(final String op_name, boolean op_checked)
	{
		name = op_name;
		checked = op_checked;
	}

	public final String getName()
	{
		return name;
	}

	@Transient
	public final Role withCheckedState(boolean state)
	{
		return new Role(name, state);
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
