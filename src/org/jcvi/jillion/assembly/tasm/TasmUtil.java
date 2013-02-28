package org.jcvi.jillion.assembly.tasm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Utility class for working with tasm files.
 * @author dkatzel
 *
 */
final class TasmUtil {

	//03/05/10 01:52:31 PM
	/**
	 * TIGR Project Database edit date format '03/05/10 01:52:31 PM'.
	 * This field is private with static format and parse synchronized
	 * methods since {@link DateFormat} is not threadsafe.
	 */
	 private static final DateFormat EDIT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy hh:mm:ss aa", Locale.US);
	
	 private TasmUtil(){
		 //can not instantiate
	 }
	 /**
	  * Parse the given string encoded
	  * Tasm edit date.
	  * @param editDate the edit date value from a tasm file.
	  * @return the edit date as a {@link Date} object;
	  * will never be null.
	  * @throws ParseException if the edit date is not formatted
	  * in the proper way for tasm files.
	  * @throws NullPointerException if editDate is null.
	  */
	 public static synchronized  Date parseEditDate(String editDate) throws ParseException{
		 if(editDate==null){
			 throw new NullPointerException("edit date can not be null");
		 }
		 return TasmUtil.EDIT_DATE_FORMAT.parse(editDate);
	 }
	 /**
	  * Format a {@link Date} in the correct String format
	  * to it can be used in a tasm file.
	  * @param editDate the {@link Date}; can not be null.
	  * @return a String in the correct tasm edit date format;
	  * will not be null.
	  * @throws NullPointerException if editDate is null.
	  */
	 public static synchronized String formatEditDate(Date editDate){
		 if(editDate==null){
			 throw new NullPointerException("edit date can not be null");
		 }
		 return TasmUtil.EDIT_DATE_FORMAT.format(editDate);
	 }
}