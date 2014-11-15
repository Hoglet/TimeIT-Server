package se.solit.timeit.entities;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User
{
	@Id
	@Column(nullable = false, name = "username")
	@OneToMany(cascade = CascadeType.REMOVE)
	private String				username;

	@Column(name = "name")
	private String				name;

	@Column(name = "email")
	private String				email;

	@Column(name = "password")
	private String				password;

	@ManyToMany
	private Collection<Role>	roles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "owner")
	private Collection<Task>	tasks;

	public User()
	{
	}

	public User(final String paramName, final String paramUsername, final String paramPassword,
			final String paramEmail, Collection<Role> roles2)
	{
		this.name = paramName;
		this.username = paramUsername;
		this.password = paramPassword;
		this.email = paramEmail;
		this.roles = roles2;
	}

	// CHECKSTYLE:OFF
	@Override
	public final int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

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
		User other = (User)obj;
		if (email == null)
		{
			if (other.email != null)
			{
				return false;
			}
		}
		else if (!email.equals(other.email))
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
		if (password == null)
		{
			if (other.password != null)
			{
				return false;
			}
		}
		else if (!password.equals(other.password))
		{
			return false;
		}
		if (username == null)
		{
			if (other.username != null)
			{
				return false;
			}
		}
		else if (!username.equals(other.username))
		{
			return false;
		}
		return true;
	}

	public final void setName(final String name)
	{
		this.name = name;
	}

	public final void setUsername(final String username)
	{
		this.username = username;
	}

	public final void setEmail(final String email)
	{
		this.email = email;
	}

	public final void setPassword(final String password)
	{
		this.password = password;
	}

	public final String getName()
	{
		return name;
	}

	public final String getUsername()
	{
		return username;
	}

	public final String getEmail()
	{
		return email;
	}

	public final String getPassword()
	{
		return password;
	}

	public void setRoles(Collection<Role> roles)
	{
		this.roles = roles;
	}

	public Collection<Role> getRoles()
	{
		return roles;
	}

	public boolean hasRole(Role op1)
	{
		return hasRole(op1.getName());
	}

	public boolean hasRole(String name)
	{
		for (Role role : roles)
		{
			if (role.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

}
