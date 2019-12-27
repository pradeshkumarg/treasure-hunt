package com.socure.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socure.dto.QuestionDTO;
import com.socure.dto.QuestionResponseDTO;
import com.socure.dto.ResponseDTO;
import com.socure.model.User;
import com.socure.repository.UserRepository;

@RestController
public class QuestionsController {

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	UserRepository userRepository;

	@PostMapping("/questions/check")
	public String check(@RequestBody QuestionDTO questionDTO) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:answers.properties");
		File file = resource.getFile();
		Properties properties = new Properties();
		InputStream in = new FileInputStream(file);
		properties.load(in);
		if (questionDTO.getAnswer().equals(properties.get(questionDTO.getId()))) {
			return "Success";
		}
		return "Failure";
	}

	@GetMapping("/question")
	public ResponseEntity<?> getQuestion(@RequestParam String token, @RequestParam Integer level) throws Exception {
		User user = userRepository.findByToken(token);
		if (user.getLevel() > level && level > 0) {
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(200);
			responseDTO.setMessage("You have already completed this level !");
			return ResponseEntity.accepted().body(responseDTO);
		} else if (user.getLevel() == level && level != 0) {
			QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO();
			questionResponseDTO.setId(user.getCurrentQuestion());
			return ResponseEntity.accepted().body(questionResponseDTO);
		} else if (level - user.getLevel() == 1) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<Map<String, List<QuestionDTO>>> typeReference = new TypeReference<Map<String, List<QuestionDTO>>>() {
			};
			Resource resource = resourceLoader.getResource("classpath:answers.json");
			File file = resource.getFile();
			InputStream inputStream = new FileInputStream(file);
			Map<String, List<QuestionDTO>> levelVsQuestionsMap = mapper.readValue(inputStream, typeReference);
			List<QuestionDTO> listOfQuestions = levelVsQuestionsMap.get(level.toString());
			QuestionResponseDTO questionResponseDTO = anyQuestion(listOfQuestions);
			user.setCurrentQuestion(questionResponseDTO.getId());
			userRepository.save(user);
			return ResponseEntity.accepted().body(questionResponseDTO);
		} else {
			ResponseDTO responseDTO = new ResponseDTO();
			responseDTO.setStatus(401);
			responseDTO.setMessage("Warning! You cannot bypass any levels.");
			return ResponseEntity.accepted().body(responseDTO);
		}
	}

	public QuestionResponseDTO anyQuestion(List<QuestionDTO> listOfQuestions) {
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(listOfQuestions.size());
		QuestionDTO questionDTO = listOfQuestions.get(index);
		QuestionResponseDTO questionResponseDTO = new QuestionResponseDTO();
		BeanUtils.copyProperties(questionDTO, questionResponseDTO);
		return questionResponseDTO;
	}
}
