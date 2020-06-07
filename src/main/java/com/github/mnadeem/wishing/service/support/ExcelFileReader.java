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

public class ExcelFileReader {

	private static Logger logger = LoggerFactory.getLogger(ExcelFileReader.class);

	private final ExcelFiles excelFiles;
	private final ResourceLoader resourceLoader;
	private final Boolean stopOnLoadError;

	public ExcelFileReader(Boolean stopOnLoadError, ResourceLoader resourceLoader, ExcelFiles excelFiles) {
		this.stopOnLoadError = stopOnLoadError;
		this.excelFiles = excelFiles;
		this.resourceLoader = resourceLoader;
	}

	public void forEach(Consumer<WishData> consumer) {
		excelFiles.forEach(excelFile -> readRows(excelFile, consumer));
	}

	private void readRows(ExcelFile excelFile, Consumer<WishData> consumer) {
		logger.debug("Reading Excel file {} ", excelFile);
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(resourceLoader.getResource(excelFile.getFileName()).getInputStream());
			Sheet sheet = workbook.getSheetAt(excelFile.getSheetNumber());
			Iterator<Row> rowsIterator = sheet.iterator();

			while (rowsIterator.hasNext()) {
				Row row = (Row) rowsIterator.next();
				if (row.getRowNum() == 0) {
					continue ;
				}
				consumer.accept(buildWishData(row, excelFile));
			}

		} catch (IOException e) {
			logger.error("Error Reading Excelbook " + excelFile, e);
			if (stopOnLoadError) {
				throw new ExcelFileReadError(e.getMessage(), e);
			}
		} finally {
			IOUtils.closeQuietly(workbook);
		}
	}

	private WishData buildWishData(Row row, ExcelFile excelFile) {
		WishData wishData = new WishData();

		wishData.setPartition(excelFile.getWorkbookNumber());
		wishData.setName((String) getCellValue(row.getCell(excelFile.getNameIndex())));
		wishData.setEmail((String) getCellValue(row.getCell(excelFile.getEmailIndex())));

		extractAndsetDate(row, "Birth date", excelFile, excelFile.getDobIndex(), (dob) -> wishData.setBirthDate(dob));
		extractAndsetDate(row, "Hire date", excelFile, excelFile.getHireIndex(), (hireDate) -> wishData.setHireDate(hireDate));

		validateRow(excelFile, wishData, row.getRowNum());

		return wishData;
	}

	private void extractAndsetDate(Row row, String column, ExcelFile excelFile, Integer cellIndex, Consumer<LocalDate> dateConsumer) {
		if (cellIndex != null) {
			dateConsumer.accept(getLocalDate(getCellValue(row.getCell(cellIndex))));
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace(column + " column not specified for {} : {}", excelFile, row);
			}
		}
	}

	private void validateRow(ExcelFile excelFile, WishData wishData, Integer rowNumber) {		
		logOrThrow("Name", excelFile, rowNumber, wishData, data -> (!StringUtils.hasText(data.getName())));
		logOrThrow("Email", excelFile, rowNumber, wishData, data -> (!StringUtils.hasText(data.getEmail())));
		logOrThrow("Hire / Birth date both", excelFile, rowNumber, wishData, data -> (data.getHireDate() == null && data.getBirthDate() == null));
	}

	private void logOrThrow(String field, ExcelFile excelFile, Integer rowNumber, WishData wishData, Predicate<WishData> predicate) {
		if (predicate.test(wishData)) {
			if (stopOnLoadError) {
				throw new ExcelFileReadError(field + " not specified for " + excelFile + " : Row " + rowNumber);
			} else {
				logger.warn(field + " not specified for {} : Row {}", excelFile, rowNumber);
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
