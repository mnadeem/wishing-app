package com.github.mnadeem.wishing.service.support;

import java.util.List;
import java.util.function.Consumer;

public class ExcelFiles {

	private final List<ExcelFile> names;

	public ExcelFiles(List<ExcelFile> names) {
		this.names = names;
	}

	public void forEach(Consumer<ExcelFile> consumer) {
		names.forEach(name-> consumer.accept(name));
	}
}
