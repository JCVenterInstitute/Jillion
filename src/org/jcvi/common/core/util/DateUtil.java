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

	private static long specifiedTime;
	private static boolean useSystemTime=true;
	private DateUtil(){}
	
	public static synchronized Date getCurrentDate(){
		if(useSystemTime){
			return new Date();
		}
		return new Date(specifiedTime);
	}
	
	public static synchronized void setCurrentDate(Date date){
		if(date ==null){
			throw new NullPointerException("specified date can not be null");
		}
		useSystemTime=false;
		specifiedTime = date.getTime();
	}
	
	public static synchronized void restoreDate(){
		useSystemTime=true;
	}
}
