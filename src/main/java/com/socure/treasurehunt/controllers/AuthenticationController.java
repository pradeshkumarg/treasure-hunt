package com.socure.treasurehunt.controllers;

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

import com.socure.treasurehunt.constants.TreasureHuntConstants;
import com.socure.treasurehunt.dto.ResponseDTO;
import com.socure.treasurehunt.dto.UserLoginDTO;
import com.socure.treasurehunt.dto.UserResponseDTO;
import com.socure.treasurehunt.model.User;
import com.socure.treasurehunt.repository.UserRepository;

@RestController
public class AuthenticationController {

	@Autowired
	UserRepository userRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse res) {
		User dbUser = userRepository.findByLoginName(userLoginDTO.getLoginName());
		ResponseDTO responseDTO = new ResponseDTO();
		if(null != dbUser && dbUser.getIsBanned()) {
			responseDTO.setStatus(401);
			responseDTO.setMessage(TreasureHuntConstants.BANNED);
			return ResponseEntity.status(401).body(responseDTO);
		}
		else if (null != dbUser) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (passwordEncoder.matches(userLoginDTO.getPassword(), dbUser.getPassword())) {
				final UserResponseDTO userResponseDTO = new UserResponseDTO();
				BeanUtils.copyProperties(dbUser, userResponseDTO);
				Cookie cookie = new Cookie("token", dbUser.getToken());
				cookie.setSecure(true);
				cookie.setHttpOnly(true);
				res.addCookie(cookie);
				return ResponseEntity.ok().body(userResponseDTO);
			}
		}
		responseDTO.setStatus(401);
		responseDTO.setMessage(TreasureHuntConstants.FAILURE);
		return ResponseEntity.status(401).body(responseDTO);
	}

	@GetMapping("/sign_in_with_token")
	public ResponseEntity<?> signInWithToken(@RequestParam String token) {
		User dbUser = userRepository.findByToken(token);
		ResponseDTO responseDTO = new ResponseDTO();
		if(null != dbUser && dbUser.getIsBanned()) {
			responseDTO.setStatus(401);
			responseDTO.setMessage(TreasureHuntConstants.BANNED);
			return ResponseEntity.status(401).body(responseDTO);
		}
		else if (null != dbUser) {
			final UserResponseDTO userResponseDTO = new UserResponseDTO();
			BeanUtils.copyProperties(dbUser, userResponseDTO);
			return ResponseEntity.ok().body(userResponseDTO);
		}
		responseDTO.setStatus(401);
		responseDTO.setMessage(TreasureHuntConstants.FAILURE);
		return ResponseEntity.status(401).body(responseDTO);
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
