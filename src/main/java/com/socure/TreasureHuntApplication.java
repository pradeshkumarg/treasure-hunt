package com.socure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.socure"})
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TreasureHuntApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TreasureHuntApplication.class, args);
	}
}
