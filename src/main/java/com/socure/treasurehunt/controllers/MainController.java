package com.socure.treasurehunt.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	@GetMapping("/")
	public String hello() {
		return "Welcome to Socure Treasure Hunt !!";
	}
}
