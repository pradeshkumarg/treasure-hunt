package com.socure.treasurehunt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = { "com.socure" })
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class TreasureHuntApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TreasureHuntApplication.class, args);
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
				.allowedOrigins("https://treasurehunt-2020.socure.net" , "https://api-treasurehunt-2020.socure.net","http://localhost:3000", "http://localhost:9000");
			}
		};
	}
}
