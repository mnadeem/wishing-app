package com.github.mnadeem.wishing.service.support.reader;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public class WishFileReadError extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public WishFileReadError(String msg) {
		super(msg);
	}
	
	public WishFileReadError(String msg, Throwable cause) {
		super(msg, cause);
	}

}
