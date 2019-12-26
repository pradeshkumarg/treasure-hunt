package com.socure.controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class MainController {
	
	@Autowired
	UserRepository userRepository;

	@GetMapping({"/", "/welcome"})
	public String welcomePage() {
		return "Hello";
	}
	
	@PostMapping("/login")
	public String login(@RequestBody User user, HttpServletResponse res) {
		User dbUser = userRepository.findByEmail(user.getEmail());
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if(passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
			Cookie cookie = new Cookie("token", dbUser.getToken());
			cookie.setSecure(true);
			cookie.setHttpOnly(true);
		    res.addCookie(cookie);
			return "Success";
		}
		return "Failure";
	}
}
