/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
/**
 * Default implementation of {@link FastaIndex}.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
public class DefaultFastaIndex implements FastaIndex{

	private static final Pattern FIELD_SEPARATOR = Pattern.compile("\t");
	
	private final Map<String, FastaIndexRecord> map;
	
	DefaultFastaIndex(Map<String, FastaIndexRecord> map) {
		this.map = map;
	}

	@Override
	public FastaIndexRecord getIndexFor(String id) {
		return map.get(id);
	}

	/**
	 * Parse the given fai encoded file into a {@link FastaIndex}
	 * object as a UTF-8 file.
	 * 
	 * @param fai the fai encoded file to be read;
	 * must exist and be readable.
	 * 
	 * @apiNote this is the same as 
	 * {@link #parse(File, String) parse(fai, "UTF-8")}
	 * 
	 * @return a new {@link FastaIndex} object; will never be null.
	 * @throws IOException if there is a problem reading the file.
	 * 
	 * @throws NullPointerException if fai is null.
	 * 
	 * @see #parse(File, String)
	 */
	public static FastaIndex parse(File fai) throws IOException{
		return parse(fai, IOUtil.UTF_8_NAME);
	}
	
	/**
	 * Parse the given fai encoded file into a {@link FastaIndex}
	 * object with the given {@link java.nio.charset.Charset} name.
	 * 
	 * @param fai the fai encoded file to be read;
	 * must exist and be readable.
	 * 
	 * @param charsetName the name of the {@link java.nio.charset.Charset} to use.
	 * 
	 * @return a new {@link FastaIndex} object; will never be null.
	 * @throws IOException if there is a problem reading the file.
	 * 
	 * @throws NullPointerException if fai is null.
	 */
	public static FastaIndex parse(File fai, String charsetName) throws IOException{
		Map<String, FastaIndexRecord> map = new HashMap<>();
		
		try(BufferedReader reader = IOUtil.createNewBufferedReader(fai, charsetName)){
			String line;
			while( (line= reader.readLine()) !=null){
				String[] fields = FIELD_SEPARATOR.split(line);
				
				FastaIndexRecord record = new FastaIndexRecord(
						Long.parseLong(fields[1]),
						Long.parseLong(fields[2]), 
						Integer.parseInt(fields[3]),
						Integer.parseInt(fields[4]));
				
				map.put(fields[0], record);
			}
		}
		
		return new DefaultFastaIndex(map);
	}
}
