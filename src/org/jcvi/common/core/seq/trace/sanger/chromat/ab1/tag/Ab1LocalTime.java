package org.jcvi.common.core.seq.trace.sanger.chromat.ab1.tag;

public final class Ab1LocalTime {

	private final int hour,min,sec;

	public Ab1LocalTime(int hour, int min, int sec) {
		this.hour = hour;
		this.min = min;
		this.sec = sec;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hour;
		result = prime * result + min;
		result = prime * result + sec;
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
		Ab1LocalTime other = (Ab1LocalTime) obj;
		if (hour != other.hour) {
			return false;
		}
		if (min != other.min) {
			return false;
		}
		if (sec != other.sec) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Ab1LocalTime [hour=" + hour + ", min=" + min + ", sec=" + sec
				+ "]";
	}

	public int getHour() {
		return hour;
	}

	public int getMin() {
		return min;
	}

	public int getSec() {
		return sec;
	}
	
	
}
