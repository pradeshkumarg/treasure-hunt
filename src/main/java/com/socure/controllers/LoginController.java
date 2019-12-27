package com.socure.controllers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
public class LoginController {

	@Autowired
	UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse res) {
		User dbUser = userRepository.findByName(user.getName());
		ResponseDTO responseDTO = new ResponseDTO();
		if (null != dbUser) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
				final UserResponseDTO userDTO = new UserResponseDTO();
				BeanUtils.copyProperties(dbUser, userDTO);
				Cookie cookie = new Cookie("token", dbUser.getToken());
				cookie.setSecure(true);
				cookie.setHttpOnly(true);
				res.addCookie(cookie);
				return ResponseEntity.accepted().body(userDTO);
			}
		}
		responseDTO.setStatus(400);
		responseDTO.setMessage(TreasureHuntConstants.FAILURE);
		return ResponseEntity.badRequest().body(responseDTO);
	}

	@GetMapping("/sign_in_with_token")
	public ResponseEntity<?> signInWithToken(@RequestParam String token) {
		User dbUser = userRepository.findByToken(token);
		ResponseDTO responseDTO = new ResponseDTO();
		if (null != dbUser) {
			final UserResponseDTO userDTO = new UserResponseDTO();
			BeanUtils.copyProperties(dbUser, userDTO);
			return ResponseEntity.accepted().body(userDTO);
		}
		responseDTO.setStatus(400);
		responseDTO.setMessage(TreasureHuntConstants.FAILURE);
		return ResponseEntity.badRequest().body(responseDTO);
	}
}
