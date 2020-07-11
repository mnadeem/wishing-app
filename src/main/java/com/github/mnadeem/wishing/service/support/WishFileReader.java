package com.github.mnadeem.wishing.service.support;

import java.util.function.Consumer;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public interface WishFileReader {
	
	void readRows(WishFile wishFile, Consumer<WishData> consumer);
}
