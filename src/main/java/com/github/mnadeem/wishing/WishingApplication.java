package com.github.mnadeem.wishing;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class WishingApplication implements CommandLineRunner {
	
	private static Logger logger = LoggerFactory.getLogger(WishingApplication.class);
	
	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(WishingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (logger.isTraceEnabled()) {
			MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
			StreamSupport.stream(propSrcs.spliterator(), false)
			        .filter(ps -> ps instanceof EnumerablePropertySource)
			        .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
			        .flatMap(Arrays::<String>stream)
			        .filter(propName -> propName.startsWith("app") || propName.startsWith("spring") || propName.startsWith("logging"))
			        .forEach(propName -> logger.trace("{} : {}", propName, env.getProperty(propName)));
		}		
	}
}
