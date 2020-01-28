package se.solit.timeit.entities;

import java.util.Collection;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import se.solit.timeit.utilities.Crypto;

@Entity
@Table(name = "users")
@Immutable
public class User
{
	@Id
	@Column(nullable = false, name = "username")
	@OneToMany(cascade = CascadeType.REMOVE)
	private String             username;

	@Column(name = "name")
	private final String             name;

	@Column(name = "email")
	private final String             email;

	@Column(name = "password")
	private final String             password;

	@ManyToMany
	private final Collection<Role>   roles;

	public User()
	{
		this(true, "", null, "", null, null);
	}

	public User(final String paramUsername, final String paramName, final String paramPassword, final String paramEmail,
	        Collection<Role> roles2)
	{
		this(true, paramUsername, paramName, Crypto.encrypt(paramPassword), paramEmail, roles2);
		
	}

	private User(boolean b, final String paramUsername, final String paramName, final String paramPassword,
	        final String paramEmail, Collection<Role> roles2)
	{
		if (paramUsername == null)
		{
			throw new NullPointerException("Username has to be set");
		}
		this.name = paramName;
		this.username = paramUsername;
		this.password = paramPassword;
		this.email = paramEmail;
		this.roles = roles2;
	}
	
	// CHECKSTYLE:OFF
	// SONAR:OFF
	@Override
	public final int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + username.hashCode();
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		User other = (User) obj;
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
		if (!username.equals(other.username))
		{
			return false;
		}

		if (roles == null)
		{
			if (other.roles != null)
			{
				return false;
			}
		}
		else if (!roles.equals(other.roles))
		{
			return false;
		}

		return true;
	}

	// SONAR:ON
	// CHECKSTYLE:ON

	public final User withName(final String name)
	{
		return new User(true,username, name, password, email, roles);
	}

	public final User withEmail(final String email)
	{
		String newEmail = email.toLowerCase(Locale.ENGLISH);
		return new User(true,username, name, password, newEmail, roles);
	}

	public final User withPassword(final String password)
	{
		return new User(username, name, password, email, roles);
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

	public User withRoles(Collection<Role> roles)
	{
		return new User(true, username, name, password, email, roles);
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
		if (roles != null)
		{
			for (Role role : roles)
			{
				if (role.getName().equals(name))
				{
					return true;
				}
			}
		}
		return false;
	}

}
