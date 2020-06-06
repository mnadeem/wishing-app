package com.github.mnadeem.wishing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class WishingApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WishingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
