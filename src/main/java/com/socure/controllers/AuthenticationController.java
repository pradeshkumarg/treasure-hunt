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
import com.socure.dto.UserLoginDTO;
import com.socure.dto.UserResponseDTO;
import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class AuthenticationController {

	@Autowired
	UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse res) {
		User dbUser = userRepository.findByName(userLoginDTO.getName());
		ResponseDTO responseDTO = new ResponseDTO();
		if (null != dbUser) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (passwordEncoder.matches(userLoginDTO.getPassword(), dbUser.getPassword())) {
				final UserResponseDTO userResponseDTO = new UserResponseDTO();
				BeanUtils.copyProperties(dbUser, userResponseDTO);
				Cookie cookie = new Cookie("token", dbUser.getToken());
				cookie.setSecure(true);
				cookie.setHttpOnly(true);
				res.addCookie(cookie);
				return ResponseEntity.accepted().body(userResponseDTO);
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
			final UserResponseDTO userResponseDTO = new UserResponseDTO();
			BeanUtils.copyProperties(dbUser, userResponseDTO);
			return ResponseEntity.accepted().body(userResponseDTO);
		}
		responseDTO.setStatus(400);
		responseDTO.setMessage(TreasureHuntConstants.FAILURE);
		return ResponseEntity.badRequest().body(responseDTO);
	}

	@PostMapping("/logout")
	public void logout(HttpServletResponse response) {
		Cookie cookie = new Cookie("token", null);
		cookie.setMaxAge(0);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
	}
}
