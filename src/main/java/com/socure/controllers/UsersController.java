package com.socure.controllers;

import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socure.constants.TreasureHuntConstants;
import com.socure.dto.ResponseDTO;
import com.socure.dto.UserResponseDTO;
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
	public ResponseEntity<?> saveUser(@RequestBody User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password = user.getPassword();
		user.setPassword(passwordEncoder.encode(password));
		user.setToken(UUID.randomUUID().toString());
		try {
			User userRet = userRepository.save(user);
			UserResponseDTO userResponseDTO = new UserResponseDTO();
			BeanUtils.copyProperties(userRet, userResponseDTO);
			return ResponseEntity.accepted().body(userResponseDTO);
		}
		catch(Exception e) {
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(400);
			responseDTO.setMessage(TreasureHuntConstants.ERROR_OCCURRED_IN_SIGNUP);
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	@GetMapping("/user/check_name_availability")
	public ResponseDTO isNameAvailable(@RequestParam String name) {
		User user = userRepository.findByName(name);
		ResponseDTO responseDTO = new ResponseDTO();
		if(null == user) {
			responseDTO.setStatus(200);
			responseDTO.setMessage("Username available");
			return responseDTO;
		}
		responseDTO.setStatus(200);
		responseDTO.setMessage("Username not available");
		return responseDTO;
	}
	
}
