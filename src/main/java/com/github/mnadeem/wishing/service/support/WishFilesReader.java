package com.github.mnadeem.wishing.service.support;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

public class WishFilesReader {
	
	private static Logger logger = LoggerFactory.getLogger(WishFilesReader.class);
	
	private final WishFiles wishFiles;
	private final ResourceLoader resourceLoader;
	private final Boolean stopOnLoadError;

	public WishFilesReader(Boolean stopOnLoadError, ResourceLoader resourceLoader, WishFiles wishFiles) {
		this.stopOnLoadError = stopOnLoadError;
		this.wishFiles = wishFiles;
		this.resourceLoader = resourceLoader;
	}

	public void forEach(Consumer<WishData> consumer) {
		wishFiles.forEach(wishFile -> readRows(wishFile, consumer));
	}

	private void readRows(WishFile wishFile, Consumer<WishData> consumer) {
		try {
			getWishFileReader(wishFile).readRows(wishFile, consumer);
		} catch (IOException e) {
			logger.error("Error Reading file : " + wishFile, e);
		}
	}

	private WishFileReader getWishFileReader(WishFile wishFile) throws IOException {
		return new ExceWishFileReader(stopOnLoadError, resourceLoader.getResource(wishFile.getFileName()).getInputStream());
	}
}
