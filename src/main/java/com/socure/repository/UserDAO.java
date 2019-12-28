package com.socure.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.socure.model.User;

@Repository
public interface UserDAO {
	public List<User> getUserContainingString(String text);
}
