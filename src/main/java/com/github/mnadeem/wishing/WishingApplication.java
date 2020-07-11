package com.github.mnadeem.wishing;

import static com.github.mnadeem.wishing.Constants.PROPERTY_NAME_BELATED_DATES;
import static com.github.mnadeem.wishing.Constants.PROPERTY_NAME_BELATED_DATE_FORMAT;
import static com.github.mnadeem.wishing.Constants.PROPERTY_NAME_BELATED_MAX_BACK_DAYS;
import static com.github.mnadeem.wishing.Constants.PROPERTY_NAME_SCHEDULE_EXTERNALLY_MANAGED;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
import org.springframework.util.StringUtils;

import com.github.mnadeem.wishing.function.ThrowingConsumer;
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

		Set<LocalDate> belatedDates = belatedDates();
		if (!belatedDates.isEmpty()) {
			logger.info("Processing Belated Dates {} ", belatedDates);
			belatedDates.forEach((ThrowingConsumer<LocalDate>) date -> job.processWishes(date));
			logger.info("Finished processing Belated Dates");
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

	private Set<LocalDate> belatedDates() {
		Set<LocalDate> result = Collections.emptySet();
		String belatedDatesPropValue = belatedDatesPropValue();
		
		if (StringUtils.hasText(belatedDatesPropValue)) {
			result = extractBelatedDates(belatedDatesPropValue);
		}

		return result;
	}

	private Set<LocalDate> extractBelatedDates(String belatedDatesPropValue) {
		Set<LocalDate> result = new HashSet<LocalDate>();
		String belatedDateFormat = belatedDateFormat();
		Integer belatedMaxBackDays = belatedMaxBackDays();

		String[] dateStrs = belatedDatesPropValue.split(",");
		for (String dateStr : dateStrs) {
			if (StringUtils.hasText(dateStr)) {
				addBelatedDate(result, belatedDateFormat, belatedMaxBackDays, dateStr);
			}
		}
		return result;
	}

	private void addBelatedDate(Set<LocalDate> result, String belatedDateFormat, Integer belatedMaxBackDays,
			String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(belatedDateFormat);
		LocalDate belatedDate = LocalDate.parse(dateStr.trim(), formatter);
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		if (belatedDate.isBefore(tomorrow) && !(ChronoUnit.DAYS.between(belatedDate, tomorrow) > belatedMaxBackDays)) {
			result.add(belatedDate);
		} else {
			logger.info("Skipped {}, as it is beyond {} days ", dateStr, belatedMaxBackDays);
		}
	}

	private Integer belatedMaxBackDays() {
		return env.<Integer>getProperty(PROPERTY_NAME_BELATED_MAX_BACK_DAYS, Integer.class, 5);
	}

	private String belatedDateFormat() {
		return env.<String>getProperty(PROPERTY_NAME_BELATED_DATE_FORMAT, String.class, "yyyy-MM-dd");
	}

	private String belatedDatesPropValue() {
		return env.<String>getProperty(PROPERTY_NAME_BELATED_DATES, String.class);
	}
}
