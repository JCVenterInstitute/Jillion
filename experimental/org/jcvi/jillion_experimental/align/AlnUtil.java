/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align;

import java.io.IOException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.TextLineParser;

public class AlnUtil {
	
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
			 throw new IllegalStateException("error reading aln header");
		 }finally{
			 IOUtil.closeAndIgnoreErrors(parser);
		 }
		 
	    	//first line of aln must say either "CLUSTAL W" or "CLUSTALW"
			//other info in first line is ignored.
	    	return header.startsWith("CLUSTAL");
			
		}
}
