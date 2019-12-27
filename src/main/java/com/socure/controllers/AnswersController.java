package com.socure.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socure.constants.TreasureHuntConstants;
import com.socure.dto.ResponseDTO;
import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class AnswersController {

	private static Map<String, String> levelVsClueMap;

	static {
		levelVsClueMap = new HashMap<>();
		levelVsClueMap.put("1", "Clue for 2");
		levelVsClueMap.put("2", "Clue for 3");
		levelVsClueMap.put("3", "Clue for 4");
		levelVsClueMap.put("4", "Clue for 5");
		levelVsClueMap.put("5", "Clue for 6");
		levelVsClueMap.put("6", "Congrats");
	}

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/check_answer")
	public ResponseEntity<?> checkAnswer(@RequestParam String token, @RequestParam String questionId,
			@RequestParam String answer) throws Exception {
		User dbUser = userRepository.findByToken(token);
		if (null != questionId && null != dbUser && dbUser.getCurrentQuestion().equals(questionId) && null != answer) {
			Integer level = TreasureHuntConstants.SOCURE.indexOf(questionId.charAt(0)) + 1;
			Resource resource = resourceLoader.getResource("classpath:answers.properties");
			File file = resource.getFile();
			Properties properties = new Properties();
			InputStream in = new FileInputStream(file);
			properties.load(in);
			if (answer.equals(properties.get(questionId))) {
				if(dbUser.getStats().contains(level.toString())) {
					ResponseDTO responseDTO = new ResponseDTO();
					responseDTO.setStatus(200);
					responseDTO.setMessage("Level already cleared. " + levelVsClueMap.get(level.toString()));
					return ResponseEntity.accepted().body(responseDTO);
				}
				dbUser.setLevel(level);
				String stats = dbUser.getStats();
				dbUser.setStats(stats.concat(" | " +level.toString()));
				userRepository.save(dbUser);
				ResponseDTO responseDTO = new ResponseDTO();
				responseDTO.setStatus(200);
				responseDTO.setMessage(levelVsClueMap.get(level.toString()));
				return ResponseEntity.accepted().body(responseDTO);

			} else {
				ResponseDTO responseDTO = new ResponseDTO();
				responseDTO.setStatus(200);
				responseDTO.setMessage("Wrong Answer");
				return ResponseEntity.accepted().body(responseDTO);
			}
		} else {
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(401);
			responseDTO.setMessage("Warning !! You are not allowed to perform the operation");
			return ResponseEntity.accepted().body(responseDTO);
		}
	}

}
