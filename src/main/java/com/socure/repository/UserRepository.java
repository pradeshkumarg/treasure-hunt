package com.socure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.socure.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	
	List<User> findAll();
	User save(User user);
	User findByName(String name);
	User findByToken(String token);

	@Query("SELECT COUNT(u) FROM User u")
    Long getTotalUsersCount();
}
