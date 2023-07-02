package com.eclectics.System;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class MemberDataApplication {
	public static void main(String[] args) {
		SpringApplication.run(MemberDataApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			System.out.println("MEMBER-DATA-SERVICE INITIALIZED SUCCESSFULLY AT:-  " + new Date());
		};
	}
}