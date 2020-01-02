package com.socure.treasurehunt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.socure.treasurehunt.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	
	List<User> findAll();

	@SuppressWarnings("unchecked")
	User save(User user);
	
	User findByLoginName(String name);
	
	User findByToken(String token);

	@Query("SELECT COUNT(u) FROM User u")
    Long getTotalUsersCount();
	
	@Query("SELECT COUNT(u) FROM User u where u.stats like '%Redeemed%'")
	Long getRedemptionCount();

	@Query("SELECT COUNT(u) FROM User u where u.isBanned is true")
	Long getBannedCount();
}
	