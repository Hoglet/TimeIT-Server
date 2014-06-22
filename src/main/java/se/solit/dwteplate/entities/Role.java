package se.solit.dwteplate.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "role")
public class Role
{
	@ManyToMany(mappedBy = "roles")
	private Collection<User>	users;

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

	@Transient
	private boolean	checked;

	@Id
	private String	name;
}
