package com.github.mnadeem.wishing.service.support;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.function.Consumer;

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
			workbook = new XSSFWorkbook(resourceLoader.getResource("classpath:" + excelFile.getFileName()).getInputStream());
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

		wishData.setName((String) getCellValue(row.getCell(excelFile.getNameIndex())));
		wishData.setEmail((String) getCellValue(row.getCell(excelFile.getEmailIndex())));
		wishData.setPartition(excelFile.getWorkbookNumber());

		if (excelFile.getDobIndex() != null) {			
			wishData.setBirthDate(getLocalDate(getCellValue(row.getCell(excelFile.getDobIndex()))));
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Birth column not specified for {}", excelFile);
			}
		}
		if (excelFile.getHireIndex() != null) {			
			wishData.setHireDate(getLocalDate(getCellValue(row.getCell(excelFile.getHireIndex()))));
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Hire column not specified for {}", excelFile);
			}
		}

		validateRow(excelFile, wishData, row.getRowNum());

		return wishData;
	}

	private void validateRow(ExcelFile excelFile, WishData wishData, Integer rowNumber) {
		if (!StringUtils.hasText(wishData.getName())) {			
			if (stopOnLoadError) {
				throw new ExcelFileReadError("Name not specified for " + excelFile + " : Row " + rowNumber);
			} else {
				logger.warn("Name not specified for {}: Row {}", excelFile, rowNumber);
			}
		} 
		
		if (!StringUtils.hasText(wishData.getEmail())) {
			if (stopOnLoadError) {
				throw new ExcelFileReadError("Email not specified for " + excelFile + " : Row " + rowNumber);
			} else {
				logger.warn("Email not specified for {}: Row {}", excelFile, rowNumber);
			}			
		}

		if (wishData.getHireDate() == null && wishData.getBirthDate() == null) {
			if (stopOnLoadError) {
				throw new ExcelFileReadError("Hire date and dob both not specified for " + excelFile + " : Row " + rowNumber);
			} else {
				logger.warn("Hire date and dob both not specified for {}: Row {}", excelFile, rowNumber);
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
