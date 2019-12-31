package com.socure.treasurehunt.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.socure.treasurehunt.model.User;

@Repository
public class UserDAOImpl implements UserDAO {

	@Autowired
	EntityManager entityManager;

	@Override
	public List<User> getUserContainingString(String text) {
		TypedQuery<User> query = entityManager.createQuery("select u from User u where lower(concat(u.name,u.loginName,u.email,u.institution,u.phone,u.stats)) like concat('%',:text,'%')", User.class);
		query.setParameter("text", text.toLowerCase());
		List<User> usersList = query.getResultList();
		return usersList;
	}

}
