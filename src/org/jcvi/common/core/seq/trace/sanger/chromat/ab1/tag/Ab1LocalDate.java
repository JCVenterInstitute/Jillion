package org.jcvi.common.core.seq.trace.sanger.chromat.ab1.tag;

import java.util.Calendar;
import java.util.Date;

public final class Ab1LocalDate {

	private final int year,month,day;

	public Ab1LocalDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Ab1LocalDate other = (Ab1LocalDate) obj;
		if (day != other.day) {
			return false;
		}
		if (month != other.month) {
			return false;
		}
		if (year != other.year) {
			return false;
		}
		return true;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}
	
	public synchronized Date toDate(Ab1LocalTime localTime){
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month, day, localTime.getHour(), localTime.getMin(), localTime.getSec());
		return cal.getTime();
	}
	
}
