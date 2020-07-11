package com.github.mnadeem.wishing.service.support;

import java.util.List;
import java.util.function.Consumer;

public class WishFiles {

	private final List<WishFile> names;

	public WishFiles(List<WishFile> names) {
		this.names = names;
	}

	public void forEach(Consumer<WishFile> consumer) {
		names.forEach(name-> consumer.accept(name));
	}
}
