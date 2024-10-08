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
package org.jcvi.jillion.assembly.consed.phd;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.ace.AceFileUtil;

/**
 * {@code PhdUtil} is a Utility class for Phred Phd data.
 * @author dkatzel
 *
 *
 */
public final class PhdUtil {
	/**
	 * Use {@link ThreadLocal} since each DateFormat instance
	 * is mutable and not Thread safe.
	 * This should let us avoid synchronization.
	 */
	private static ThreadLocal<DateFormat> PHD_TAG_DATE_FORMATTER = new ThreadLocal<DateFormat>(){

		  @Override
		  public DateFormat get() {
		   return super.get();
		  }

		  @Override
		  protected DateFormat initialValue() {
		   return new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.US);
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
	
	private PhdUtil(){
		//can not instantiate
	}
    /**
     * Parse the given Phd formatted date string into a {@link Date}.
     * @param dateString the date string to parse;
     * can not be null.
     * @return a new Date; will never be null.
     * @throws ParseException if the date String is in the 
     * wrong format.
     */
	public static Date parseReadTagDate(String dateString) throws ParseException{
		return PHD_TAG_DATE_FORMATTER.get().parse(dateString);
	}
	/**
	 * Format the given date into the String
	 * representation used by Phd tags.
	 * @param date the date to format;
	 * can not be null.
	 * @return a new String will never be null or empty.
	 */
	public static String formatReadTagDate(Date date){
		return PHD_TAG_DATE_FORMATTER.get().format(date);
	}
    /**
     * Phd records must include a date time stamp as a comment,
     * this method will create the correctly formatted Phd {@code TIME}
     * comment.  
     * @param phdDate the {@link Date} to make into a Phd TIME
     * comment.
     * @return a Properties object (not null) that contains
     * a single property, TIME.
     */
    public static Map<String,String> createPhdTimeStampCommentFor(Date phdDate){
    	Map<String,String> comments = new HashMap<String, String>();
        comments.put("TIME", AceFileUtil.formatPhdDate(phdDate));        
        return comments;
    }
    /**
     * Phd records must include a date time stamp as a comment,
     * this method will create the correctly formatted Phd {@code TIME}
     * comment.  
     * @param phdDate the {@link Date} to make into a Phd TIME
     * comment.
     * @param filename the name of the chromatogram file to link back to.
     * @return a Properties object (not null) that contains
     * a single property, TIME.
     */
    public static Map<String,String> createPhdTimeStampAndChromatFileCommentsFor(Date phdDate, String filename){
    	Map<String,String> comments = new HashMap<String, String>();
        comments.put("TIME", AceFileUtil.formatPhdDate(phdDate));    
        comments.put("CHROMAT_FILE", filename);  
        return comments;
    }
}
