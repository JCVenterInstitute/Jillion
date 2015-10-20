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
package org.jcvi.jillion.experimental.align;

import java.io.IOException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.TextLineParser;

public final class AlnUtil {
	
	private AlnUtil(){
		//can not instantiate
	}
	/**
	 * Checks to see if the given header string
	 * is valid. A valid header is a single line
	 * that starts with the text
	 * "CLUSTAL".
	 * @param header the header to validate; can not be null.
	 * @return {@code true}
	 * if the header is valid,
	 * {@code false} otherwise.
	 * @throws NullPointerException if header is null
	 * @throws IllegalStateException if there is a problem
	 * parsing the header text (should not happen).
	 */
	 public static boolean validHeader(String header) {
		 if(header.isEmpty()){
			 return false;
		 }
		 //check is one line?
		 TextLineParser parser=null;
		 try{
			 parser = new TextLineParser(IOUtil.toInputStream(header));
			 parser.nextLine();
			 if(parser.hasNextLine()){
				 return false;
			 }
		 }catch(IOException e){
			 //will never happen
			 throw new IllegalStateException("error reading aln header", e);
		 }finally{
			 IOUtil.closeAndIgnoreErrors(parser);
		 }
		 
	    	//first line of aln must say either "CLUSTAL W" or "CLUSTALW"
			//other info in first line is ignored.
	    	return header.startsWith("CLUSTAL");
			
		}
}
