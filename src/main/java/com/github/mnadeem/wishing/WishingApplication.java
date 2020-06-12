package com.github.mnadeem.wishing;

import java.time.LocalDate;
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

import static com.github.mnadeem.wishing.Constants.*;

import com.github.mnadeem.wishing.job.WishingJob;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class WishingApplication implements CommandLineRunner {

	private static final String PROPERTY_NAME_PASSWORD = "password";
	private static final String PROPERTY_NAME_CREDENTIALS = "credentials";
	private static final String PROPERTY_NAME_LOGGING = "logging";
	private static final String PROPERTY_NAME_SPRING = "spring";

	private static Logger logger = LoggerFactory.getLogger(WishingApplication.class);

	@Autowired
	private Environment env;
	@Autowired
	private WishingJob job;

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
			        .filter(propName -> isPropertyValid(propName))
			        .forEach(propName -> logger.trace("{} : {}", propName, env.getProperty(propName)));
		}

		if (externallyManaged()) {
			logger.info("Trigger externally managed, and hence running the process only once");
			job.processWishes(LocalDate.now());
		} else {
			logger.info("Trigger internally managed, and hence process would keep on running until forcibly stopped");
		}
	}

	private boolean isPropertyValid(String propName) {
		return (propName.startsWith("app") || propName.startsWith(PROPERTY_NAME_SPRING) || propName.startsWith(PROPERTY_NAME_LOGGING)) && (!propName.contains(PROPERTY_NAME_CREDENTIALS) || !propName.contains(PROPERTY_NAME_PASSWORD));
	}

	private Boolean externallyManaged() {
		return env.<Boolean>getProperty(PROPERTY_NAME_SCHEDULE_EXTERNALLY_MANAGED, Boolean.class, Boolean.FALSE);
	}
}
