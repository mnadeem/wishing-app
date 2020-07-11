package com.github.mnadeem.wishing.service.support.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.springframework.core.io.ResourceLoader;

import com.github.mnadeem.wishing.service.support.data.WishData;
import com.github.mnadeem.wishing.service.support.data.WishFile;
import com.github.mnadeem.wishing.service.support.data.WishFiles;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public class WishFilesReader {

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
		getWishFileReader(wishFile).readRows(wishFile, consumer);
	}

	private WishFileReader getWishFileReader(WishFile wishFile) {
		InputStream stream;
		try {
			stream = resourceLoader.getResource(wishFile.getFileName()).getInputStream();
		} catch (IOException e) {
			throw new WishFileReadError("Can read file : " + wishFile);
		}

		WishFileReader wishFileReader = null;
		if (wishFile.isCsv()) {
			wishFileReader = new CsvWishFileReader(stopOnLoadError, stream);
		} else if(wishFile.isXlsx()) {
			wishFileReader = new ExceWishFileReader(stopOnLoadError, stream);
		} else {
			throw new IllegalArgumentException("Invalid Wish file");
		}
		return wishFileReader;
	}
}
