package com.github.mnadeem.wishing.job;

public interface WishingJob {
	
	void processWishes() throws Exception;	
	void ping() throws Exception;
}
