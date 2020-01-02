package com.socure.treasurehunt.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.socure.treasurehunt.model.User;

@Repository
public interface UserDAO {
	public List<User> getUserContainingString(String text);

	public List<User> getBannedUsersContainingString(String text);
}
