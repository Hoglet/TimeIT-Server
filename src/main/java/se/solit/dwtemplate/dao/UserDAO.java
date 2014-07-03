package se.solit.dwtemplate.dao;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import se.solit.dwteplate.entities.User;

public class UserDAO
{
	private final EntityManagerFactory	emf;

	public UserDAO(EntityManagerFactory emf)
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

	public User getUser(String username)
	{
		EntityManager em = emf.createEntityManager();
		User user = em.find(User.class, username);
		em.close();
		return user;
	}

	public void update(User user)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(user);
		em.getTransaction().commit();
		em.close();
	}

	public void delete(User user)
	{
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.remove(em.getReference(User.class, user.getUsername()));
		em.getTransaction().commit();
		em.close();
	}

}
