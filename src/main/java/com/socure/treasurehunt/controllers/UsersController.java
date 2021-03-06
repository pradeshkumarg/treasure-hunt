package com.socure.treasurehunt.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socure.treasurehunt.constants.TreasureHuntConstants;
import com.socure.treasurehunt.dto.ResponseDTO;
import com.socure.treasurehunt.dto.UserInputDTO;
import com.socure.treasurehunt.dto.UserMetricDTO;
import com.socure.treasurehunt.model.Metric;
import com.socure.treasurehunt.model.User;
import com.socure.treasurehunt.repository.UserDAO;
import com.socure.treasurehunt.repository.UserRepository;

@RestController
public class UsersController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserDAO userDAO;

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
		user.setCurrentQuestion("");
		user.setCurrentClue(TreasureHuntConstants.LEVEL_0_CLUE);
		try {
			userRepository.save(user);
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(201);
			responseDTO.setMessage(TreasureHuntConstants.SIGN_UP_SUCCESSFULL);
			return ResponseEntity.created(null).body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(400);
			responseDTO.setMessage(TreasureHuntConstants.ERROR_OCCURRED_IN_SIGNUP);
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@GetMapping("/user/check_name_availability")
	public ResponseDTO isNameAvailable(@RequestParam String loginName, HttpServletResponse httpServletResponse) {
		User user = userRepository.findByLoginName(loginName);
		ResponseDTO responseDTO = new ResponseDTO();
		if (null == user) {
			responseDTO.setStatus(200);
			responseDTO.setMessage("Username available");
			return responseDTO;
		}
		httpServletResponse.setStatus(406);
		responseDTO.setStatus(406);
		responseDTO.setMessage("Username not available");
		return responseDTO;
	}

	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/user/stats/{name}")
	public ResponseDTO getStats(@PathVariable String name, HttpServletResponse httpServletResponse) {
		ResponseDTO responseDTO = new ResponseDTO();
		User user = userRepository.findByLoginName(name);
		if (null != user) {
			responseDTO.setStatus(200);
			responseDTO.setMessage(user.getStats());
			return responseDTO;
		}
		httpServletResponse.setStatus(404);
		responseDTO.setStatus(404);
		responseDTO.setMessage("User not found");
		return responseDTO;
	}

	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@PostMapping("/user/redeem/{name}")
	public ResponseDTO redeemUser(@PathVariable String name, HttpServletResponse httpServletResponse) {
		ResponseDTO responseDTO = new ResponseDTO();
		User user = userRepository.findByLoginName(name);
		if (null != user) {
			String stats = user.getStats();
			if (stats.contains("Redeemed")) {
				httpServletResponse.setStatus(403);
				responseDTO.setStatus(403);
				responseDTO.setMessage("Redeemed Already");
				return responseDTO;
			} else if(user.getIsBanned()) {
				responseDTO.setStatus(401);
				httpServletResponse.setStatus(401);
				responseDTO.setMessage("User has been banned");
				return responseDTO;
			}
			else if (stats.contains("6")) {
				user.setStats(stats + " | Redeemed");
				userRepository.save(user);
				responseDTO.setStatus(200);
				responseDTO.setMessage("Redeemed Successfully");
				return responseDTO;
			} else {
				httpServletResponse.setStatus(401);
				responseDTO.setStatus(401);
				responseDTO.setMessage("Cannot Redeem token");
				return responseDTO;
			}
		}
		httpServletResponse.setStatus(404);
		responseDTO.setStatus(404);
		responseDTO.setMessage("User not found");
		return responseDTO;
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/users")
	public List<UserMetricDTO> getAllUsers() {
		List<User> usersList = userRepository.findAll();
		List<UserMetricDTO> userMetricDTOList = new ArrayList<>();
		for (User user : usersList) {
			UserMetricDTO userMetricDTO = new UserMetricDTO();
			BeanUtils.copyProperties(user, userMetricDTO);
			userMetricDTOList.add(userMetricDTO);
		}
		return userMetricDTOList;
	}

	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/user/search/{text}")
	public List<UserMetricDTO> getUsersList(@PathVariable String text) {
		List<User> usersList = userDAO.getUserContainingString(text);
		List<UserMetricDTO> userMetricDTOList = new ArrayList<>();
		for (User user : usersList) {
			UserMetricDTO userMetricDTO = new UserMetricDTO();
			BeanUtils.copyProperties(user, userMetricDTO);
			userMetricDTOList.add(userMetricDTO);
		}
		return userMetricDTOList;
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/banned_users")
	public List<UserMetricDTO> getBannedUsers(@RequestParam String text) {
		List<User> usersList = userDAO.getBannedUsersContainingString(text);
		List<UserMetricDTO> userMetricDTOList = new ArrayList<>();
		for (User user : usersList) {
			UserMetricDTO userMetricDTO = new UserMetricDTO();
			BeanUtils.copyProperties(user, userMetricDTO);
			userMetricDTOList.add(userMetricDTO);
		}
		return userMetricDTOList;
	}

	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/users/count")
	public ResponseEntity<?> getUsersCount() {
		Long count = userRepository.getTotalUsersCount();
		return ResponseEntity.ok().body(count);
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/redemptions")
	public ResponseEntity<?> getRedemptionCount() {
		Long count = userRepository.getRedemptionCount();
		return ResponseEntity.ok().body(count);
	}

	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@PutMapping("/user/reset_password")
	public ResponseEntity<?> resetPassword(@RequestParam String name) {
		ResponseDTO responseDTO = new ResponseDTO();
		User user = userRepository.findByLoginName(name);
		if (null != user) {
			String newPassword = UUID.randomUUID().toString().substring(0, 6);
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			responseDTO.setData(newPassword);
			responseDTO.setMessage("Password Reset Successfully");
			responseDTO.setStatus(200);
			return ResponseEntity.status(200).body(responseDTO);
		}
		responseDTO.setStatus(404);
		responseDTO.setMessage("User not found");
		return ResponseEntity.status(404).body(responseDTO);
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@PutMapping("/user/ban")
	public ResponseEntity<?> banUser(@RequestParam String name) {
		ResponseDTO responseDTO = new ResponseDTO();
		User user = userRepository.findByLoginName(name);
		Metric metric = new Metric();
		if(null != user) {
			metric.setUser(user);
			metric.setSeverity("High");
			metric.setStatus("User Banned !");
			user.setIsBanned(true);
			userRepository.save(user);
			responseDTO.setMessage("User Banned !");
			responseDTO.setStatus(200);
			return ResponseEntity.status(200).body(responseDTO);
		}
		responseDTO.setStatus(404);
		responseDTO.setMessage("User not found");
		return ResponseEntity.status(404).body(responseDTO);
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@PutMapping("/user/unban")
	public ResponseEntity<?> unBanUser(@RequestParam String name) {
		ResponseDTO responseDTO = new ResponseDTO();
		User user = userRepository.findByLoginName(name);
		Metric metric = new Metric();
		if(null != user && user.getIsBanned()) {
			metric.setUser(user);
			metric.setSeverity("High");
			metric.setStatus("User Unbanned");
			user.setIsBanned(false);
			userRepository.save(user);
			responseDTO.setMessage("User Unbanned !");
			responseDTO.setStatus(200);
			return ResponseEntity.status(200).body(responseDTO);
		} else if(null != user) {
			responseDTO.setStatus(200);
			responseDTO.setMessage("This user is active");
			return ResponseEntity.status(200).body(responseDTO);
		}
		responseDTO.setStatus(404);
		responseDTO.setMessage("User not found");
		return ResponseEntity.status(404).body(responseDTO);
	}
	
	@CrossOrigin({ "http://localhost:9000", "https://cryptic-headland-55422.herokuapp.com" })
	@GetMapping("/ban_count")
	public Long getBannedCount() {
		Long bannedCount = userRepository.getBannedCount();
		return bannedCount;
	}
	
}
