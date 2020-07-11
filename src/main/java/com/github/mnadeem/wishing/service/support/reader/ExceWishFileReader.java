package com.github.mnadeem.wishing.service.support.reader;

import java.io.IOException;
import java.io.InputStream;
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

import com.github.mnadeem.wishing.service.support.data.WishData;
import com.github.mnadeem.wishing.service.support.data.WishFile;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public class ExceWishFileReader extends BaseFileReader {

	private static Logger logger = LoggerFactory.getLogger(ExceWishFileReader.class);

	private final InputStream stream;

	public ExceWishFileReader(Boolean stopOnLoadError, InputStream inputStream) {
		super(stopOnLoadError);
		this.stream = inputStream;
	}

	@Override
	public void readRows(WishFile wishFile, Consumer<WishData> consumer) {
		logger.debug("Reading Excel file {} ", wishFile);
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(stream);
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
			IOUtils.closeQuietly(stream);
		}
	}

	private WishData buildWishData(Row row, WishFile wishFile) {
		WishData wishData = new WishData();

		wishData.setPartition(wishFile.getWorkbookNumber());
		wishData.setName((String) getCellValue(row.getCell(wishFile.getNameIndex())));
		wishData.setEmail((String) getCellValue(row.getCell(wishFile.getEmailIndex())));

		extractAndsetDate(row, "Birth date", wishFile, wishFile.getDobIndex(), (dob) -> wishData.setBirthDate(dob));
		extractAndsetDate(row, "Hire date", wishFile, wishFile.getHireIndex(), (hireDate) -> wishData.setHireDate(hireDate));

		validateRow(wishFile, wishData, (long) row.getRowNum());

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
