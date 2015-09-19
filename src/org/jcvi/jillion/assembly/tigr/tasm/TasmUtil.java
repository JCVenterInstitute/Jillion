/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.tasm;

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
	 * <p/>
	 * Use {@link ThreadLocal} since each DateFormat instance
	 * is mutable and not Thread safe.
	 * This should let us avoid synchronization.
	 */
	private static ThreadLocal<DateFormat> EDIT_DATE_FORMAT = new ThreadLocal<DateFormat>(){

		  @Override
		  public DateFormat get() {
		   return super.get();
		  }

		  @Override
		  protected DateFormat initialValue() {
		   return new SimpleDateFormat("MM/dd/yy hh:mm:ss aa", Locale.US);
		  }

		  @Override
		  public void remove() {
		   super.remove();
		  }

		  @Override
		  public void set(DateFormat value) {
		   super.set(value);
		  }

		 };
		
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
	 public static  Date parseEditDate(String editDate) throws ParseException{
		 if(editDate==null){
			 throw new NullPointerException("edit date can not be null");
		 }
		 return TasmUtil.EDIT_DATE_FORMAT.get().parse(editDate);
	 }
	 /**
	  * Format a {@link Date} in the correct String format
	  * to it can be used in a tasm file.
	  * @param editDate the {@link Date}; can not be null.
	  * @return a String in the correct tasm edit date format;
	  * will not be null.
	  * @throws NullPointerException if editDate is null.
	  */
	 public static String formatEditDate(Date editDate){
		 if(editDate==null){
			 throw new NullPointerException("edit date can not be null");
		 }
		 return TasmUtil.EDIT_DATE_FORMAT.get().format(editDate);
	 }
}
