package com.github.mnadeem.wishing.service.data;

import java.time.LocalDate;
import java.time.Period;

public class Wish  {
	private String name;
	private String email;
	private String wish;
	private LocalDate wishDate;
	private String detail;
	private WishType wishType;

	private Wish(WishType wishType) {
		this.wishType = wishType;
	}

	public static Wish birthdayWish() {
		return new Wish(WishType.BIRTHDAY);
	}

	public static Wish anniversaryWish() {
		return new Wish(WishType.ANNIVERSARY);
	}
	
	public boolean isBirthday() {
		return WishType.BIRTHDAY == getWishType();
	}
	public int getYears() {
		return Period.between(wishDate, LocalDate.now()).getYears();
	}

	public String getYearsMessage() {
		String message = getYears() + " Year";
		if (getYears() > 1) {
			message = message + "s";
		}
		return message;
	}

	public WishType getWishType() {
		return wishType;
	}

	public void setWish(String wish) {
		this.wish = wish;
	}

	public void setWishDate(LocalDate wishDate) {
		this.wishDate = wishDate;
	}

	public String getWish() {
		return wish;
	}

	public LocalDate getWishDate() {
		return wishDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Override
	public String toString() {
		return "Wish [name=" + name + ", email=" + email + ", wish=" + wish + ", wishDate=" + wishDate
				+ ", detail=" + detail + "]";
	}

	public enum WishType {
		BIRTHDAY, ANNIVERSARY;
	}
}
