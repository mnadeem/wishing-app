package com.github.mnadeem.wishing.service.support;

public class ExcelFileReadError extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ExcelFileReadError(String msg) {
		super(msg);
	}
	
	public ExcelFileReadError(String msg, Throwable cause) {
		super(msg, cause);
	}

}
