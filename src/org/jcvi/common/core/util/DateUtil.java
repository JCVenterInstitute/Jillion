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

	private static Date specifiedDate=null;
	
	private DateUtil(){}
	
	public static synchronized Date getCurrentDate(){
		if(specifiedDate ==null){
			return new Date();
		}
		return new Date(specifiedDate.getTime());
	}
	
	public static synchronized void setCurrentDate(Date date){
		if(date ==null){
			specifiedDate =null;
		}else{
			specifiedDate = new Date(date.getTime());
		}
	}
	
	public static synchronized void restoreDate(){
		setCurrentDate(null);
	}
}
