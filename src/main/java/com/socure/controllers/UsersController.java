package com.socure.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class UsersController {

	@Autowired
	UserRepository userRepository;

	@GetMapping("/users/sample")
	public User getUser() {
		User user = new User();
		return user;
	}

	@PostMapping("/users")
	public User saveUser(@RequestBody User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = user.getPassword();
		user.setPassword(passwordEncoder.encode(password));
		User userRet = userRepository.save(user);
		return userRet;
	}
}
