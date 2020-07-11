package com.github.mnadeem.wishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.github.mnadeem.wishing.Constants.*;
/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
@Configuration
public class WishingConfiguration {

	private static final String THREAD_NAME_PREFIX = "Wishing-Async-";
	private static final int DEFAULT_MAX_POOL_SIZE = 10;
	private static final int DEFAULT_CORE_POOL_SIZE = 5;

	@Autowired
	private Environment env;

	@Bean("taskExecutor")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(getCorePoolSize());
	    executor.setMaxPoolSize(getMaxPoolSize());
	    executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
		return executor;
	}

	private Integer getCorePoolSize() {
		return env.<Integer>getProperty(PROPERTY_NAME_THREAD_CORE_POOL_SIZE, Integer.class, DEFAULT_CORE_POOL_SIZE);
	}

	private Integer getMaxPoolSize() {
		return env.<Integer>getProperty(PROPERTY_NAME_THREAD_MAX_POOL_SIZE, Integer.class, DEFAULT_MAX_POOL_SIZE);
	}
}
