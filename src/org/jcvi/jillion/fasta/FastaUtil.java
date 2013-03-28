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
package org.jcvi.jillion.fasta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * {@code FastaUtil} is a utility class
 * for common Fasta constants.
 * @author dkatzel
 *
 *
 */
public final class FastaUtil {
	/**
	 * The line String used to separate lines in 
	 * a Fasta file.  This Separator is platform
	 * independent.
	 */
    public static final String LINE_SEPARATOR = "\n";
    /**
     * The required prefix in a fasta header '{@value}'.
     */
    public static final char HEADER_PREFIX = '>';
    
    
    public static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");

    private FastaUtil(){
    	
    }
    

    
    public static String parseCommentFromDefLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.find()){
        	String comment= idMatcher.group(3);
        	if(comment ==null){
        		return null;
        	}
            if(!comment.isEmpty()){
            	return comment;
            }
        }
        return null;
    }

    public static String parseIdFromDefLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.find()){
            return idMatcher.group(1);           
        }
        return null;
    }
    
}
