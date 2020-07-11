package com.github.mnadeem.wishing.service.support;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

public class WishFileReader {

	private static Logger logger = LoggerFactory.getLogger(WishFileReader.class);

	private final WishFiles wishFiles;
	private final ResourceLoader resourceLoader;
	private final Boolean stopOnLoadError;

	public WishFileReader(Boolean stopOnLoadError, ResourceLoader resourceLoader, WishFiles wishFiles) {
		this.stopOnLoadError = stopOnLoadError;
		this.wishFiles = wishFiles;
		this.resourceLoader = resourceLoader;
	}

	public void forEach(Consumer<WishData> consumer) {
		wishFiles.forEach(wishFile -> readRows(wishFile, consumer));
	}

	private void readRows(WishFile wishFile, Consumer<WishData> consumer) {
		logger.debug("Reading wish file {} ", wishFile);
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(resourceLoader.getResource(wishFile.getFileName()).getInputStream());
			Sheet sheet = workbook.getSheetAt(wishFile.getSheetNumber());
			Iterator<Row> rowsIterator = sheet.iterator();

			while (rowsIterator.hasNext()) {
				Row row = (Row) rowsIterator.next();
				if (row.getRowNum() == 0) {
					continue ;
				}
				consumer.accept(buildWishData(row, wishFile));
			}

		} catch (IOException e) {
			logger.error("Error Reading Excelbook " + wishFile, e);
			if (stopOnLoadError) {
				throw new WishFileReadError(e.getMessage(), e);
			}
		} finally {
			IOUtils.closeQuietly(workbook);
		}
	}

	private WishData buildWishData(Row row, WishFile wishFile) {
		WishData wishData = new WishData();

		wishData.setPartition(wishFile.getWorkbookNumber());
		wishData.setName((String) getCellValue(row.getCell(wishFile.getNameIndex())));
		wishData.setEmail((String) getCellValue(row.getCell(wishFile.getEmailIndex())));

		extractAndsetDate(row, "Birth date", wishFile, wishFile.getDobIndex(), (dob) -> wishData.setBirthDate(dob));
		extractAndsetDate(row, "Hire date", wishFile, wishFile.getHireIndex(), (hireDate) -> wishData.setHireDate(hireDate));

		validateRow(wishFile, wishData, row.getRowNum());

		return wishData;
	}

	private void extractAndsetDate(Row row, String column, WishFile wishFile, Integer cellIndex, Consumer<LocalDate> dateConsumer) {
		if (cellIndex != null) {
			dateConsumer.accept(getLocalDate(getCellValue(row.getCell(cellIndex))));
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace(column + " column not specified for {} : {}", wishFile, row);
			}
		}
	}

	private void validateRow(WishFile wishFile, WishData wishData, Integer rowNumber) {		
		logOrThrow("Name", wishFile, rowNumber, wishData, data -> (!StringUtils.hasText(data.getName())));
		logOrThrow("Email", wishFile, rowNumber, wishData, data -> (!StringUtils.hasText(data.getEmail())));
		logOrThrow("Hire / Birth date both", wishFile, rowNumber, wishData, data -> (data.getHireDate() == null && data.getBirthDate() == null));
	}

	private void logOrThrow(String field, WishFile wishFile, Integer rowNumber, WishData wishData, Predicate<WishData> predicate) {
		if (predicate.test(wishData)) {
			if (stopOnLoadError) {
				throw new WishFileReadError(field + " not specified for " + wishFile + " : Row " + rowNumber);
			} else {
				logger.warn(field + " not specified for {} : Row {}", wishFile, rowNumber);
			}	
		} 
	}

	private static Object getCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}
		Object result = null;
		switch (cell.getCellType()) {
		case STRING:
			result = cell.getStringCellValue();
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				result = cell.getDateCellValue();
			} else {
				result = cell.getNumericCellValue();
			}
			break;
		default:
			break;
		}

		return result;
	}

	private static LocalDate getLocalDate(Object obj) {
		if (!(obj instanceof Date)) {
			return null;
		}
		LocalDate localDate = ((Date) obj).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
	}
}
