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
