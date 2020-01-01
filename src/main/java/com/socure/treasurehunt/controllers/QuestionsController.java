package com.socure.treasurehunt.controllers;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socure.treasurehunt.constants.TreasureHuntConstants;
import com.socure.treasurehunt.dto.QuestionDTO;
import com.socure.treasurehunt.dto.ResponseDTO;
import com.socure.treasurehunt.model.Metric;
import com.socure.treasurehunt.model.User;
import com.socure.treasurehunt.repository.MetricsRepository;
import com.socure.treasurehunt.repository.UserRepository;

@RestController
public class QuestionsController {

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MetricsRepository metricRepository;

	@GetMapping("/question")
	public ResponseEntity<?> getQuestion(@RequestParam String token, @RequestParam Integer level) throws Exception {
		User user = userRepository.findByToken(token);
		Metric metric = new Metric();
		ResponseDTO responseDTO = new ResponseDTO();
		if(null != user && user.getIsBanned()) {
			responseDTO.setStatus(401);
			responseDTO.setMessage(TreasureHuntConstants.BANNED);
			return ResponseEntity.status(401).body(responseDTO);
		}
		else if (null != user) {
			metric.setUser(user);
			if (user.getLevel() > level || user.getStats().contains(level.toString())) {
				metric.setStatus("Scanned a completed QR code in Level "+ level);
				metric.setSeverity("low");
				metricRepository.save(metric);
				responseDTO.setStatus(400);
				responseDTO.setMessage("You have already completed this level !");
				return ResponseEntity.badRequest().body(responseDTO);
			} else if (level != 0 && !user.getCurrentQuestion().equals("") && level == getLevelByCurrentQuestion(user.getCurrentQuestion())) {
				QuestionDTO questionDTO = new QuestionDTO();
				questionDTO.setId(user.getCurrentQuestion());
				return ResponseEntity.ok().body(questionDTO);
			} else if (level - user.getLevel() == 1 && level <= 6) {
				ObjectMapper mapper = new ObjectMapper();
				TypeReference<Map<String, List<QuestionDTO>>> typeReference = new TypeReference<Map<String, List<QuestionDTO>>>() {
				};
				Resource resource = resourceLoader.getResource("classpath:questions.json");
				InputStream inputStream = resource.getInputStream();
				Map<String, List<QuestionDTO>> levelVsQuestionsMap = mapper.readValue(inputStream, typeReference);
				List<QuestionDTO> listOfQuestions = levelVsQuestionsMap.get(level.toString());
				QuestionDTO questionDTO = anyQuestion(listOfQuestions);
				user.setCurrentQuestion(questionDTO.getId());
				userRepository.save(user);
				return ResponseEntity.ok().body(questionDTO);
			} else {
				Integer actualLevel = user.getLevel();
				actualLevel = actualLevel < 6 ? actualLevel+1 : actualLevel;
				metric.setStatus("Tried to bypass levels. Level Scanned : "+ level + ", Actual Level : "+ actualLevel);
				metric.setSeverity("high");
				metricRepository.save(metric);
				responseDTO.setStatus(401);
				responseDTO.setMessage("Warning! You cannot bypass any levels.");
				return ResponseEntity.badRequest().body(responseDTO);
			}
		} else {
			metric.setSeverity("high");
			metric.setStatus("Tried to enter a random token ! Token : "+ token + ", Level : "+ level);
			metricRepository.save(metric);
			responseDTO.setStatus(404);
			responseDTO.setMessage("User not found.");
			return ResponseEntity.badRequest().body(responseDTO);
		}

	}

	public QuestionDTO anyQuestion(List<QuestionDTO> listOfQuestions) {
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(listOfQuestions.size());
		QuestionDTO questionDTO = listOfQuestions.get(index);
		return questionDTO;
	}

	public Integer getLevelByCurrentQuestion(String questionId) {
		Integer level = TreasureHuntConstants.SOCURE.indexOf(questionId.charAt(0)) + 1;
		return level;
	}
}
