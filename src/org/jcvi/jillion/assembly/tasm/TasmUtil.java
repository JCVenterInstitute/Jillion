package org.jcvi.jillion.assembly.tasm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class TasmUtil {

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
	 
	 public synchronized static Date parseEditDate(String editDate) throws ParseException{
		 return TasmUtil.EDIT_DATE_FORMAT.parse(editDate);
	 }
	 
	 public synchronized static String formatEditDate(Date editDate){
		 return TasmUtil.EDIT_DATE_FORMAT.format(editDate);
	 }
}
