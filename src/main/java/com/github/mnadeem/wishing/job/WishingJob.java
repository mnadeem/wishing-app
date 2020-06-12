package com.github.mnadeem.wishing.job;

import java.time.LocalDate;

public interface WishingJob {
	
	void processWishes(LocalDate date) throws Exception;	
	void ping() throws Exception;
}
