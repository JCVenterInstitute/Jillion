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
	 * object.
	 * 
	 * @param fai the fai encoded file to be read;
	 * must exist and be readable.
	 * 
	 * @return a new {@link FastaIndex} object; will never be null.
	 * @throws IOException if there is a problem reading the file.
	 * 
	 * @throws NullPointerException if fai is null.
	 */
	public static FastaIndex parse(File fai) throws IOException{
		Map<String, FastaIndexRecord> map = new HashMap<>();
		
		try(BufferedReader reader = IOUtil.createNewBufferedReader(fai, IOUtil.UTF_8_NAME)){
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
