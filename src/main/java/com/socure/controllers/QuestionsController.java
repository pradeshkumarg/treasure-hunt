package com.socure.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.socure.dto.QuestionDTO;

@RestController
public class QuestionsController {

	@Autowired
	ResourceLoader resourceLoader;
	
	@PostMapping("/questions/check")
	public String check(@RequestBody QuestionDTO questionDTO) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:answers.properties");
		File file = resource.getFile();
		Properties properties = new Properties();
		InputStream in = new FileInputStream(file);
        properties.load(in);
        if(questionDTO.getAnswer().equals(properties.get(questionDTO.getId()))) {
        	return "Success";
        }
		return "Failure";
	}
}
