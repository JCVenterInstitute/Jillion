package org.jcvi.jillion.core.util;

import java.util.Date;
/**
 * Utility class for modifying the
 * current system time, useful for testing
 * or fixing the date to a specific moment in time.
 * @author dkatzel
 *
 */
public final class DateUtil {

	
	private static final long SECONDS = 1000;
	private static final long MINS = SECONDS *60;
	
	private static final long HOURS = MINS*60;
	
	private static final long DAYS = HOURS*24;
	
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
	
	/**
	 * Get a formatted elapsed time for the given
	 * number of milliseconds.  For example
	 * if the number of milliseconds was equivalent to
	 * 21 hours, 5 minutes and 52 seconds, then this method 
	 * will return a string with the value of
	 * {@code P21H5M52S}.
	 * @param numberOfMillis the number of milliseconds elapsed;
	 * must be >=0
	 * @return a String 
	 */
	public static String getElapsedTimeAsString(long numberOfMillis){
		if(numberOfMillis< SECONDS){
			return "P0S";
		}
		StringBuilder builder = new StringBuilder("P");
		long millisLeft = numberOfMillis;
		long numDays = millisLeft/DAYS;
		if(numDays >0){
			builder.append(numDays).append('D');
			millisLeft = millisLeft - numDays*DAYS;
		}
		long numHours = millisLeft/HOURS;
		if(numHours >0){
			builder.append(numHours).append('H');
			millisLeft = millisLeft - numHours*HOURS;
		}
		long numMins = millisLeft/MINS;
		if(numMins >0){
			builder.append(numMins).append('M');
			millisLeft = millisLeft - numMins*MINS;
		}
		long numSeconds = millisLeft/SECONDS;
		if(numSeconds >0){
			builder.append(numSeconds).append('S');
			millisLeft = millisLeft - numSeconds*SECONDS;
		}
		
		return builder.toString();
	}
}
