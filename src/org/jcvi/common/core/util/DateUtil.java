package org.jcvi.common.core.util;

import java.util.Date;
/**
 * Utility class for modifying the
 * current system time, useful for testing
 * or fixing the date to a specific moment in time.
 * @author dkatzel
 *
 */
public final class DateUtil {

	private static long SPECIFIED_TIME;
	private static boolean USE_SYSTEM_TIME=true;
	private DateUtil(){}
	/**
	 * Get the current Date from either the system
	 * or whatever Date has been specified by
	 * {@link #setCurrentDate(Date)}.
	 * @return a new {@link Date}; never null.
	 * @see #setCurrentDate(Date)
	 */
	public static synchronized Date getCurrentDate(){
		if(USE_SYSTEM_TIME){
			return new Date();
		}
		return new Date(SPECIFIED_TIME);
	}
	/**
	 * Set the Date to return when {@link #getCurrentDate()}
	 * is called. Copies of this Date will be returned by
	 * {@link #getCurrentDate()} until either a new Date is set
	 * or until {@link #restoreDate()} is called.
	 * @param date the Date to use; can not be null.
	 * @see #restoreDate()
	 * @see #getCurrentDate()
	 */
	public static synchronized void setCurrentDate(Date date){
		if(date ==null){
			throw new NullPointerException("specified date can not be null");
		}
		USE_SYSTEM_TIME=false;
		SPECIFIED_TIME = date.getTime();
	}
	/**
	 * Change {@link #getCurrentDate()} to return the 
	 * current system time.
	 */
	public static synchronized void restoreDate(){
		USE_SYSTEM_TIME=true;
	}
}
