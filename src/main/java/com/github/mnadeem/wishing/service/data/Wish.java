package com.github.mnadeem.wishing.service.data;

import java.time.LocalDate;
import java.time.Period;

public class Wish  {
	private String name;
	private String email;
	private String wish;
	private LocalDate eventDate;
	private String detail;
	private WishType wishType;
	private int partition;

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
		return Period.between(eventDate, LocalDate.now()).getYears();
	}

	public String getYearsMessage() {
		String message = getYears() + " Year";
		if (getYears() > 1) {
			message = message + "s";
		}
		return message;
	}
	
	public boolean shouldWish() {
		return getYears() > 0;
	}
	
	public WishType getWishType() {
		return wishType;
	}

	public void setWish(String wish) {
		this.wish = wish;
	}
	
	
	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}


	public String getWish() {
		return wish;
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
	
	public WishKey getWishKey() {
		return new WishKey(eventDate.getMonthValue(), eventDate.getDayOfMonth());
	}

	public int getPartition() {
		return partition;
	}

	public void setPartition(int partition) {
		this.partition = partition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((wishType == null) ? 0 : wishType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wish other = (Wish) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (wishType != other.wishType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Wish [name=" + name + ", email=" + email + ", wish=" + wish + ", eventDate="
				+ eventDate + ", wishType=" + wishType + "]";
	}

	public enum WishType {
		BIRTHDAY, ANNIVERSARY;
	}
	
	public static class WishKey {
		private int month;
		private int dayOfTheMonth;

		public WishKey(int month, int dayOfTheMonth) {
			super();
			this.month = month;
			this.dayOfTheMonth = dayOfTheMonth;
		}
		public int getMonth() {
			return month;
		}
		public int getDayOfTheMonth() {
			return dayOfTheMonth;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + dayOfTheMonth;
			result = prime * result + month;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WishKey other = (WishKey) obj;
			if (dayOfTheMonth != other.dayOfTheMonth)
				return false;
			if (month != other.month)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "WishKey [month=" + month + ", dayOfTheMonth=" + dayOfTheMonth + "]";
		}
		
	}
}
