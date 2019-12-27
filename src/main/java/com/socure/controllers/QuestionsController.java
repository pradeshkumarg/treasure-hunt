package com.socure.controllers;

import java.io.File;
import java.io.FileInputStream;
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
import com.socure.dto.QuestionDTO;
import com.socure.dto.ResponseDTO;
import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class QuestionsController {

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/question")
	public ResponseEntity<?> getQuestion(@RequestParam String token, @RequestParam Integer level) throws Exception {
		User user = userRepository.findByToken(token);
		if (null != user) {
			if (user.getLevel() > level || user.getStats().contains(level.toString())) {
				ResponseDTO responseDTO = new ResponseDTO();
				responseDTO.setStatus(200);
				responseDTO.setMessage("You have already completed this level !");
				return ResponseEntity.accepted().body(responseDTO);
			} else if (user.getLevel() == level && level != 0) {
				QuestionDTO questionDTO = new QuestionDTO();
				questionDTO.setId(user.getCurrentQuestion());
				return ResponseEntity.accepted().body(questionDTO);
			} else if (level - user.getLevel() == 1 && level<=6) {
				ObjectMapper mapper = new ObjectMapper();
				TypeReference<Map<String, List<QuestionDTO>>> typeReference = new TypeReference<Map<String, List<QuestionDTO>>>() {
				};
				Resource resource = resourceLoader.getResource("classpath:questions.json");
				File file = resource.getFile();
				InputStream inputStream = new FileInputStream(file);
				Map<String, List<QuestionDTO>> levelVsQuestionsMap = mapper.readValue(inputStream, typeReference);
				List<QuestionDTO> listOfQuestions = levelVsQuestionsMap.get(level.toString());
				QuestionDTO questionDTO = anyQuestion(listOfQuestions);
				user.setCurrentQuestion(questionDTO.getId());
				userRepository.save(user);
				return ResponseEntity.accepted().body(questionDTO);
			} else {
				ResponseDTO responseDTO = new ResponseDTO();
				responseDTO.setStatus(401);
				responseDTO.setMessage("Warning! You cannot bypass any levels.");
				return ResponseEntity.accepted().body(responseDTO);
			}
		} else {
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(404);
			responseDTO.setMessage("User not found.");
			return ResponseEntity.accepted().body(responseDTO);
		}

	}

	public QuestionDTO anyQuestion(List<QuestionDTO> listOfQuestions) {
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(listOfQuestions.size());
		QuestionDTO questionDTO = listOfQuestions.get(index);
		return questionDTO;
	}
}
