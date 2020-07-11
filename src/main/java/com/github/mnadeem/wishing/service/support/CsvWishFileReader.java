package com.github.mnadeem.wishing.service.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvWishFileReader implements WishFileReader {

	private static Logger logger = LoggerFactory.getLogger(CsvWishFileReader.class);

	private final Boolean stopOnLoadError;
	private final InputStream stream;

	public CsvWishFileReader(Boolean stopOnLoadError, InputStream inputStream) {
		this.stopOnLoadError = stopOnLoadError;
		this.stream = inputStream;
	}

	@Override
	public void readRows(WishFile wishFile, Consumer<WishData> consumer) {
		InputStreamReader input = new InputStreamReader(stream);
		try {
			CSVParser csvParser = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().parse(input);
			for (CSVRecord record : csvParser) {
				consumer.accept(buildWishData(record));
			}
		} catch (IOException e) {
			logger.error("Error Reading CSV file : " + wishFile, e);
		}		
	}

	private WishData buildWishData(CSVRecord record) {
		WishData data = new WishData();
		return data;
	}
}
