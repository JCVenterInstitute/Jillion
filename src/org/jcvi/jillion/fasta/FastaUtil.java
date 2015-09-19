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
