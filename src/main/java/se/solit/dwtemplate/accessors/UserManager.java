package se.solit.dwtemplate.accessors;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import se.solit.dwtemplate.User;

public class UserManager
{
	private final EntityManagerFactory	emf;

	public UserManager(EntityManagerFactory emf)
	{
		this.emf = emf;
	}

	@SuppressWarnings("unchecked")
	public Collection<User> getUsers()
	{
		EntityManager em = emf.createEntityManager();
		Collection<User> result = em.createQuery("SELECT u FROM User u").getResultList();
		em.close();
		return result;
	}

	public void add(User user)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
		em.close();
	}

}
