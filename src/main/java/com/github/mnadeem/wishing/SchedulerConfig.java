package com.github.mnadeem.wishing;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import static com.github.mnadeem.wishing.Constants.*;

import com.github.mnadeem.wishing.job.WishingJob;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
	
	private static Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

	@Autowired
	private Environment env;

	@Autowired
	private WishingJob job;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		if (!externallyManaged()) {
			addTriggers(taskRegistrar);
		}		
	}

	private void addTriggers(ScheduledTaskRegistrar taskRegistrar) {
		pingTrigger(taskRegistrar);
		processWishTrigger(taskRegistrar);
	}

	private void pingTrigger(ScheduledTaskRegistrar taskRegistrar) {
		Trigger pingTrigger = new Trigger() {
			
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				CronTrigger cronTrigger = new CronTrigger(getSchedule(PROPERTY_NAME_CORN_PING_SCHEDULE));
				return cronTrigger.nextExecutionTime(triggerContext);
			}
		};
		taskRegistrar.addTriggerTask(() -> {
			try {
				job.ping();
			} catch (Exception e) {
				logger.error("Error executing " + e.getMessage());
			}
		}, pingTrigger);
	}
	
	private void processWishTrigger(ScheduledTaskRegistrar taskRegistrar) {
		Trigger processWishTrigger = new Trigger() {
			
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				CronTrigger cronTrigger = new CronTrigger(getSchedule(PROPERTY_NAME_CORN_SCHEDULE));
				return cronTrigger.nextExecutionTime(triggerContext);
			}
		};
		taskRegistrar.addTriggerTask(() -> {
			try {
				job.processWishes();
			} catch (Exception e) {
				logger.error("Error executing " + e.getMessage());
			}
		}, processWishTrigger);
	}

	private Boolean externallyManaged() {
		return env.<Boolean>getProperty(PROPERTY_NAME_SCHEDULE_EXTERNALLY_MANAGED, Boolean.class, Boolean.FALSE);
	}
	
	private String getSchedule(String key) {
		return env.<String>getProperty(key, String.class);
	}
}
