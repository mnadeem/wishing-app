package com.github.mnadeem.wishing.service.support.reader;

import java.util.function.Consumer;

import com.github.mnadeem.wishing.service.support.data.WishData;
import com.github.mnadeem.wishing.service.support.data.WishFile;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public interface WishFileReader {
	
	void readRows(WishFile wishFile, Consumer<WishData> consumer);
}
