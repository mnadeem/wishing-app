package com.github.mnadeem.wishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class WishingConfiguration {

	@Autowired
	private Environment env;

	@Bean("taskExecutor")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(getCorePoolSize());
	    executor.setMaxPoolSize(getMaxPoolSize());
	    executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Wishing-Async-");
		return executor;
	}

	private Integer getCorePoolSize() {
		return env.<Integer>getProperty("app.pool.thread.core_size", Integer.class, 5);
	}

	private Integer getMaxPoolSize() {
		return env.<Integer>getProperty("app.pool.thread.max_size", Integer.class, 10);
	}
}
