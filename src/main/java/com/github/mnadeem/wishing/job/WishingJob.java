package com.github.mnadeem.wishing.job;

import java.time.LocalDate;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public interface WishingJob {
	
	void processWishes(LocalDate date) throws Exception;	
	void ping() throws Exception;
}
