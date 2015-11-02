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
package org.jcvi.jillion.internal.fasta;

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
    private static final String LINE_SEPARATOR = "\n";
    /**
     * The required prefix in a fasta header '{@value}'.
     */
    private static final char HEADER_PREFIX = '>';
    
    
   
    private FastaUtil(){
    	
    }
    /**
	 * The line String used to separate lines in 
	 * a Fasta file.  This Separator is platform
	 * independent.
	 */
    public static String getLineSeparator(){
    	return LINE_SEPARATOR;
    }
    /**
     * The required prefix in a fasta header '{@value}'.
     */
    public static char getHeaderPrefix(){
    	return HEADER_PREFIX;
    }
    
}
