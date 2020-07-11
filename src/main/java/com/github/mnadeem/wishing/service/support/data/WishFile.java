package com.github.mnadeem.wishing.service.support.data;

import java.time.format.DateTimeFormatter;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
public class WishFile {

	private String fileName;
	private int nameIndex;
	private int emailIndex;
	private Integer dobIndex;
	private Integer hireIndex;
	private int sheetNumber;
	private int workbookNumber;
	private DateTimeFormatter dobFormatter;
	private DateTimeFormatter hireFormatter;
	
	public boolean isCsv() {
		return fileName != null && fileName.trim().toLowerCase().endsWith(".csv");
	}
	
	public boolean isXlsx() {
		return fileName != null && fileName.trim().toLowerCase().endsWith(".xlsx");
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setNameIndex(int nameIndex) {
		this.nameIndex = nameIndex;
	}

	public void setEmailIndex(int emailIndex) {
		this.emailIndex = emailIndex;
	}

	public void setDobIndex(Integer dobIndex) {
		this.dobIndex = dobIndex;
	}

	public void setHireIndex(Integer hireIndex) {
		this.hireIndex = hireIndex;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getEmailIndex() {
		return emailIndex;
	}

	public Integer getDobIndex() {
		return dobIndex;
	}

	public Integer getHireIndex() {
		return hireIndex;
	}

	public String getFileName() {
		return fileName;
	}

	public int getSheetNumber() {
		return sheetNumber;
	}

	public void setSheetNumber(int sheetNumber) {
		this.sheetNumber = sheetNumber;
	}

	public int getWorkbookNumber() {
		return workbookNumber;
	}

	public void setWorkbookNumber(int workbookNumber) {
		this.workbookNumber = workbookNumber;
	}

	public void setDobFormat(String dobFormat) {
		this.dobFormatter = DateTimeFormatter.ofPattern(dobFormat);
	}
	
	public DateTimeFormatter getDobFormatter() {
		return this.dobFormatter;
	}

	public void setHireFormat(String hireFormat) {
		this.hireFormatter = DateTimeFormatter.ofPattern(hireFormat);
	}
	
	public DateTimeFormatter getHireFormatter() {
		return this.hireFormatter;
	}

	@Override
	public String toString() {
		return "WishFile [fileName=" + fileName + ", nameIndex=" + nameIndex + ", emailIndex=" + emailIndex
				+ ", dobIndex=" + dobIndex + ", hireIndex=" + hireIndex + ", sheetNumber=" + sheetNumber
				+ ", workbookNumber=" + workbookNumber + "]";
	}

}
