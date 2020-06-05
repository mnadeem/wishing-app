package com.github.mnadeem.wishing.service.support;

public class ExcelFile {

	private String fileName;
	private int nameIndex;
	private int emailIndex;
	private int dobIndex;
	private int hireIndex;
	private int sheetNumber;
	private int workbookNumber;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setNameIndex(int nameIndex) {
		this.nameIndex = nameIndex;
	}

	public void setEmailIndex(int emailIndex) {
		this.emailIndex = emailIndex;
	}

	public void setDobIndex(int dobIndex) {
		this.dobIndex = dobIndex;
	}

	public void setHireIndex(int hireIndex) {
		this.hireIndex = hireIndex;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getEmailIndex() {
		return emailIndex;
	}

	public int getDobIndex() {
		return dobIndex;
	}

	public int getHireIndex() {
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

	@Override
	public String toString() {
		return "ExcelFile [fileName=" + fileName + ", nameIndex=" + nameIndex + ", emailIndex=" + emailIndex
				+ ", dobIndex=" + dobIndex + ", hireIndex=" + hireIndex + ", sheetNumber=" + sheetNumber + "]";
	}
}
