package com.github.mnadeem.wishing.service.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public class CsvWishFileReader extends BaseFileReader {

	private static Logger logger = LoggerFactory.getLogger(CsvWishFileReader.class);

	private final InputStream stream;

	public CsvWishFileReader(Boolean stopOnLoadError, InputStream inputStream) {
		super(stopOnLoadError);
		this.stream = inputStream;
	}

	@Override
	public void readRows(WishFile wishFile, Consumer<WishData> consumer) {
		InputStreamReader input = new InputStreamReader(stream);
		try {
			CSVParser csvParser = CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().parse(input);
			for (CSVRecord record : csvParser) {
				consumer.accept(buildWishData(record, wishFile));
			}
		} catch (IOException e) {
			logger.error("Error Reading CSV file : " + wishFile, e);
		}		
	}

	private WishData buildWishData(CSVRecord record, WishFile wishFile) {
		WishData wishData = new WishData();
		wishData.setPartition(wishFile.getWorkbookNumber());
		wishData.setName(record.get(wishFile.getNameIndex()));
		wishData.setEmail(record.get(wishFile.getEmailIndex()));
	
		extractAndsetDate(record, "Birth date", wishFile, wishFile.getDobIndex(), wishFile.getDobFormatter(), (dob) -> wishData.setBirthDate(dob));
		extractAndsetDate(record, "Hire date", wishFile, wishFile.getHireIndex(), wishFile.getHireFormatter(), (hireDate) -> wishData.setHireDate(hireDate));

		validateRow(wishFile, wishData, record.getRecordNumber());

		return wishData;
	}

	private void extractAndsetDate(CSVRecord record, String column, WishFile wishFile, Integer cellIndex, DateTimeFormatter formatter, Consumer<LocalDate> dateConsumer) {
		if (cellIndex != null) {
			dateConsumer.accept(LocalDate.parse(record.get(cellIndex), formatter));
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace(column + " column not specified for {} : {}", wishFile, record);
			}
		}
	}
}
