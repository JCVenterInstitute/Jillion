/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
	
	private static final DateFormat PHD_TAG_DATE_FORMATTER = new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.US);
	   
	
	private PhdUtil(){
		//can not instantiate
	}
    
	public synchronized static Date parseReadTagDate(String dateString) throws ParseException{
		return PHD_TAG_DATE_FORMATTER.parse(dateString);
	}
	
	public synchronized static String formatReadTagDate(Date date){
		return PHD_TAG_DATE_FORMATTER.format(date);
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
