package com.moducrafter.appMod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AppModHackathonApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppModHackathonApplication.class, args);
	}

}
