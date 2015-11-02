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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion.internal.core.io.TextLineParser;

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
    
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
    private static final Pattern REDUNDANT_ID_LINE_PATTERN = Pattern.compile("(\\S+)(\\s+(.*))?");

    private static final char CONTROL_A = 0x1;
   
    
    private static final Pattern NON_REDUNDANT_SPLIT = Pattern.compile(Pattern.quote(Character.toString(CONTROL_A)));
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
    
    
    public static char getNonRedundantSeparator(){
    	return CONTROL_A;
    }
    
    public static void createIndex(File fastaFile, PrintWriter out, Function<String, Integer> numberOfBases) throws IOException{
    	try(InputStream in = InputStreamSupplier.forFile(fastaFile).get();
    		TextLineParser parser = new TextLineParser(in);
    	){
    		while(parser.hasNextLine()){
    			handleNextFastaRecord(parser, out, numberOfBases);
    		}
    	}
    }
	private static void handleNextFastaRecord(TextLineParser parser, PrintWriter out, Function<String, Integer> numberOfBases) throws IOException {
		String line = parser.nextLine();
		while(line!=null && line.charAt(0) != HEADER_PREFIX){
			line = parser.nextLine();
		}
		if(line ==null){
			//EOF
			return;
		}
		
		//TODO handle non-redundant fasta with control A chars
		
		
		Matcher matcher = ID_LINE_PATTERN.matcher(line);
		if(!matcher.find()){
			throw new IllegalStateException("invalid fasta file defline ='" + line.trim() + "'");
		}
		//trim off trailing whitespace and leading '>'
		String[] redundantIds = NON_REDUNDANT_SPLIT.split(line.trim().substring(1));
		
		String id =matcher.group(1);
		long sequenceStart = parser.getPosition();
		
		String firstLine = parser.nextLine();
		long currentPosition = parser.getPosition();
		long numberOfBytesPerLineIncludingEol = currentPosition - sequenceStart;
			
		int numberOfBasesPerLine = numberOfBases.apply(firstLine);
		long seqLength = numberOfBasesPerLine;
		
		while(parser.hasNextLine() && parser.peekLine().charAt(0) != HEADER_PREFIX){
			String nextLine = parser.nextLine();
			long lastPosition =currentPosition;
			currentPosition = parser.getPosition();
			long bytesInThisLine = currentPosition - lastPosition;
			if(bytesInThisLine != numberOfBytesPerLineIncludingEol &&
					//last line can be less
					(!lastLineOfRecord(parser) || bytesInThisLine > numberOfBytesPerLineIncludingEol)){

				throw new IOException(
						String.format("invalid fasta file, different length seq lines in record '%s', all but last row must be %d bytes, but line at offset %d was %d bytes", 
								id, numberOfBytesPerLineIncludingEol, lastPosition, bytesInThisLine));
			}
			
			int numBases = numberOfBases.apply(nextLine);
			if(numBases != numberOfBasesPerLine &&
					//last line can be less
					(!lastLineOfRecord(parser) || numBases > numberOfBasesPerLine)){
				throw new IOException(
						String.format("invalid fasta file, different length seq lines in record '%s', all but last row must be %d bases, but line at offset %d was %d bases", 
								id, numberOfBasesPerLine, lastPosition, numBases));
			}
			seqLength+= numBases;
		}
		for(String redundantId : redundantIds){
			List<String> fields = new ArrayList<>(5);
			Matcher m = REDUNDANT_ID_LINE_PATTERN.matcher(redundantId);
			if(!m.find()){
				throw new IOException("error parsing id from non-redundant line: " + redundantId);
			}
			fields.add(m.group(1));
			fields.add(Long.toString(seqLength));
			fields.add(Long.toString(sequenceStart));
			fields.add(Long.toString(numberOfBasesPerLine));
			fields.add(Long.toString(numberOfBytesPerLineIncludingEol));
			
			out.println(JoinedStringBuilder.create(fields).glue('\t').build());
		}
		
		
	}
	
	private static boolean lastLineOfRecord(TextLineParser parser){
		return !parser.hasNextLine() || parser.peekLine().charAt(0) == HEADER_PREFIX;
	}
}
