package com.github.mnadeem.wishing.service.support;

import java.util.function.Consumer;

public interface WishFileReader {
	
	void readRows(WishFile wishFile, Consumer<WishData> consumer);
}
