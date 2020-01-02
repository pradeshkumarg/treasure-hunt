package com.socure.treasurehunt.controllers;

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

import com.socure.treasurehunt.constants.TreasureHuntConstants;
import com.socure.treasurehunt.dto.ResponseDTO;
import com.socure.treasurehunt.model.Metric;
import com.socure.treasurehunt.model.User;
import com.socure.treasurehunt.repository.MetricsRepository;
import com.socure.treasurehunt.repository.UserRepository;

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

	@Autowired
	MetricsRepository metricRepository;

	@GetMapping("/check_answer")
	public ResponseEntity<?> checkAnswer(@RequestParam String token, @RequestParam String questionId,
			@RequestParam String answer) throws Exception {
		User dbUser = userRepository.findByToken(token);
		ResponseDTO responseDTO = new ResponseDTO();
		if (null != dbUser && dbUser.getIsBanned()) {
			responseDTO.setStatus(401);
			responseDTO.setMessage(TreasureHuntConstants.BANNED);
			return ResponseEntity.status(401).body(responseDTO);
		}
		Metric metric = new Metric();
		metric.setUser(dbUser);
		Integer level = TreasureHuntConstants.SOCURE.indexOf(questionId.charAt(0)) + 1;
		if (null != questionId && null != dbUser && dbUser.getCurrentQuestion().equals(questionId) && null != answer) {
			Resource resource = resourceLoader.getResource("classpath:answers.properties");
			Properties properties = new Properties();
			InputStream in = resource.getInputStream();
			properties.load(in);
			if (answer.equalsIgnoreCase(properties.get(questionId).toString())) {
				if (dbUser.getStats().contains(level.toString())) {
					metric.setStatus("Answered the same Question again in Level " + level);
					metric.setSeverity("low");
					metricRepository.save(metric);
					responseDTO.setStatus(200);
					responseDTO.setMessage("Level already cleared. " + levelVsClueMap.get(level.toString()));
					return ResponseEntity.ok().body(responseDTO);
				}
				dbUser.setLevel(level);
				String stats = dbUser.getStats();
				dbUser.setStats(stats.concat(" | Level " + level.toString()));
				dbUser.setCurrentClue(levelVsClueMap.get(level.toString()));
				userRepository.save(dbUser);
				responseDTO.setStatus(200);
				responseDTO.setMessage(levelVsClueMap.get(level.toString()));
				return ResponseEntity.ok().body(responseDTO);

			} else {
				responseDTO.setStatus(406);
				responseDTO.setMessage("Wrong Answer");
				return ResponseEntity.ok().body(responseDTO);
			}
		} 
		else if(null == dbUser) {
			metric.setStatus("Tried to enter a random token. Token :"+token+" , Level : "+level);
			metric.setSeverity("high");
			metricRepository.save(metric);
		}
		else {
			Integer actualLevel = dbUser.getLevel();
			actualLevel = actualLevel < 6 ? actualLevel + 1 : actualLevel;
			if (level != 0) {
				metric.setStatus("Tried to answer Level " + level + ". Actual Level is " + actualLevel);
			} else {
				metric.setStatus("Tried to enter a random value to question id");
			}
			metric.setSeverity("high");
			metricRepository.save(metric);
		}
		responseDTO.setStatus(401);
		responseDTO.setMessage("Warning !! You are not allowed to perform the operation");
		return ResponseEntity.badRequest().body(responseDTO);
	}

}
