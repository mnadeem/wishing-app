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

public class ExcelFileReader {

	private static Logger logger = LoggerFactory.getLogger(ExcelFileReader.class);

	private final ExcelFiles excelFiles;
	private final ResourceLoader resourceLoader;

	public ExcelFileReader(ResourceLoader resourceLoader, ExcelFiles excelFiles) {
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
		} finally {
			IOUtils.closeQuietly(workbook);
		}
	}

	private WishData buildWishData(Row row, ExcelFile excelFile) {
		WishData wishData = new WishData();

		wishData.setName(String.valueOf(getCellValue(row.getCell(excelFile.getNameIndex()))));
		wishData.setEmail(String.valueOf(getCellValue(row.getCell(excelFile.getEmailIndex()))));
		wishData.setPartition(excelFile.getWorkbookNumber());
		if (excelFile.getDobIndex() != null) {			
			wishData.setBirthDate(getLocalDate(getCellValue(row.getCell(excelFile.getDobIndex()))));
		}
		if (excelFile.getHireIndex() != null) {			
			wishData.setHireDate(getLocalDate(getCellValue(row.getCell(excelFile.getHireIndex()))));
		}
		
		return wishData;
	}

	private static Object getCellValue(Cell cell) {
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
