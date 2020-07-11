package com.github.mnadeem.wishing.service.support;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public abstract class BaseFileReader implements WishFileReader {

	private static Logger logger = LoggerFactory.getLogger(BaseFileReader.class);
	
	protected final Boolean stopOnLoadError;

	public BaseFileReader(Boolean stopOnLoadError) {
		this.stopOnLoadError = stopOnLoadError;
	}

	protected void validateRow(WishFile wishFile, WishData wishData, Long rowNumber) {		
		logOrThrow("Name", wishFile, rowNumber, wishData, data -> (!StringUtils.hasText(data.getName())));
		logOrThrow("Email", wishFile, rowNumber, wishData, data -> (!StringUtils.hasText(data.getEmail())));
		logOrThrow("Hire / Birth date both", wishFile, rowNumber, wishData, data -> (data.getHireDate() == null && data.getBirthDate() == null));
	}

	protected void logOrThrow(String field, WishFile wishFile, Long rowNumber, WishData wishData, Predicate<WishData> predicate) {
		if (predicate.test(wishData)) {
			if (stopOnLoadError) {
				throw new WishFileReadError(field + " not specified for " + wishFile + " : Row " + rowNumber);
			} else {
				logger.warn(field + " not specified for {} : Row {}", wishFile, rowNumber);
			}	
		} 
	}
}
