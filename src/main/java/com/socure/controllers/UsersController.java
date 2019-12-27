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
import com.socure.dto.UserInputDTO;
import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class UsersController {

	@Autowired
	UserRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<?> saveUser(@RequestBody UserInputDTO userInputDTO) {
		String password = userInputDTO.getPassword();
		User user = new User();
		BeanUtils.copyProperties(userInputDTO, user);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		user.setPassword(passwordEncoder.encode(password));
		user.setToken(UUID.randomUUID().toString());
		user.setStats("Registered");
		user.setLevel(0);
		try {
			userRepository.save(user);
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(200);
			responseDTO.setMessage(TreasureHuntConstants.SIGN_UP_SUCCESSFULL);
			return ResponseEntity.accepted().body(responseDTO);
		} catch (Exception e) {
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
		if (null == user) {
			responseDTO.setStatus(200);
			responseDTO.setMessage("Username available");
			return responseDTO;
		}
		responseDTO.setStatus(200);
		responseDTO.setMessage("Username not available");
		return responseDTO;
	}

}
