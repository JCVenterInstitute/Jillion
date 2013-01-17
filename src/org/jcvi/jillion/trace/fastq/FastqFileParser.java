/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.trace.fastq.FastqFileVisitor.EndOfBodyReturnCode;
/**
 * {@code FastqFileParser} parses FASTQ encoded files
 * and calls callback methods
 * on the given {@link FastqFileVisitor}.
 * @author dkatzel
 *
 */
public final class FastqFileParser {
    
	private static final Pattern CASAVA_1_8_DEFLINE_PATTERN = Pattern.compile("^@(\\S+\\s+\\d:[N|Y]:\\d+:\\S+)\\s*$");
    
	private FastqFileParser(){
		//can not instantiate
	}
	/**
	 * Parse the given fastq encoded file and call the appropriate
	 * visit callbacks from the given visitor.
	 * @param fastqFile the fastq encoded file to parse; can not be null.
	 * @param visitor the {@link FastqFileVisitor} to call
	 * the visit methods on; can not be null.
	 * @throws IOException if there is a problem parsing the file.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static void parse(File fastqFile, FastqFileVisitor visitor ) throws IOException{
        InputStream in = new FileInputStream(fastqFile);
        try{
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
	/**
	 * Parse the given fastq encoded {@link InputStream} and call the appropriate
	 * visit callbacks from the given visitor. The given input stream
	 * may not be closed after parsing has finished (or if parsing
	 * is interrupted by a thrown exception) so it is up
	 * to the client to make sure to close the stream.
	 * @param fastqStream the fastq encoded {@link InputStream} to parse; can not be null.
	 * @param visitor the {@link FastqFileVisitor} to call
	 * the visit methods on; can not be null.
	 * @throws IOException if there is a problem parsing the stream.
	 * @throws NullPointerException if either parameter is null.
	 */
    public static void parse(InputStream fastqStream, FastqFileVisitor visitor ) throws IOException{
    	if(fastqStream ==null){
    		throw new NullPointerException("input stream can not be null");
    	}
    	if(visitor==null){
    		throw new NullPointerException("visitor can not be null");
    	}
    	TextLineParser parser = new TextLineParser(new BufferedInputStream(fastqStream));
		
        parse(visitor, parser);
    }
    
	private static void parse(FastqFileVisitor visitor, TextLineParser parser) {
		visitor.visitFile();
        boolean keepParsing=true;
        try{
	        while(keepParsing && parser.hasNextLine()){
				//keepParsing =parseSingleRecord(visitor, parser);
	        	keepParsing = parseNextRecord(visitor, parser);
	        }
        }catch(IOException e){
        	throw new IllegalStateException("error reading fastq file",e);
        }
        visitor.visitEndOfFile();
	}
	
	
	
	private static boolean parseNextRecord(FastqFileVisitor visitor, TextLineParser parser) throws IOException{
		String deflineText = parser.nextLine();
		visitor.visitLine(deflineText);
		Defline defline = Defline.parse(deflineText);
        FastqFileVisitor.DeflineReturnCode deflineRet= visitor.visitDefline(defline.getId(), defline.getComment());
        if(deflineRet ==null){
        	throw new IllegalStateException("defline return value can not be null");
        }
        if(deflineRet == FastqFileVisitor.DeflineReturnCode.STOP_PARSING){
        	return false;
        }
        boolean visitBody = deflineRet==FastqFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD;
        long numberOfQualities =parseSequence(visitor, parser,visitBody);
        parseQualities(visitor, parser, numberOfQualities,visitBody);
        return handleEndOfBody(visitor, visitBody);
        
        
	}
	private static boolean handleEndOfBody(FastqFileVisitor visitor,
			boolean visitBody) {
		if(visitBody){
	        EndOfBodyReturnCode endOfBodyRet = visitor.visitEndOfBody();
	        if(endOfBodyRet ==null){
	        	throw new IllegalStateException("end of body return value can not be null");
	        }
			return endOfBodyRet==EndOfBodyReturnCode.KEEP_PARSING;
        }
    	return true;

	}
	private static long parseSequence(FastqFileVisitor visitor,
			TextLineParser parser, boolean visitBody) throws IOException {
		boolean inBasecallBlock;
		//default to 200 bp since most sequences are only that much anyway
        //builder will grow if we get too big
        NucleotideSequenceBuilder sequenceBuilder = new NucleotideSequenceBuilder(200);
        String line = parser.nextLine();
    	visitor.visitLine(line);
    	sequenceBuilder.append(line.trim());
        do{
        	line = parser.nextLine();
        	visitor.visitLine(line);
        	Matcher beginQualityMatcher =FastqUtil.QUAL_DEFLINE_PATTERN.matcher(line);
        	inBasecallBlock = !beginQualityMatcher.find();
        	if(inBasecallBlock){
        		sequenceBuilder.append(line.trim());
        	}
        }while(inBasecallBlock);
        NucleotideSequence sequence = sequenceBuilder.build();
        if(visitBody){
        	visitor.visitNucleotides(sequence);
        }
        return sequence.getLength();
	}
	
	private static void parseQualities(FastqFileVisitor visitor,
			TextLineParser parser, long expectedQualities, boolean visitBody) throws IOException {
		//default to 200 bp since most sequences are only that much anyway
        //builder will grow if we get too big
        StringBuilder sequenceBuilder = new StringBuilder(200);
       
    	while(sequenceBuilder.length() < expectedQualities){
    		String line = parser.nextLine();
	    	visitor.visitLine(line);
	    	sequenceBuilder.append(line.trim());
    	}
    	if(visitBody){
    		visitor.visitEncodedQualities(sequenceBuilder.toString());
    	}
        
	}
	
	private static final class Defline{
		private final String id,comment;

		private Defline(String id, String comment) {
			this.id = id;
			this.comment = comment;
		}
		
		public static Defline parse(String fastqDefline){
			Matcher casava18Matcher = CASAVA_1_8_DEFLINE_PATTERN.matcher(fastqDefline);
			if(casava18Matcher.matches()){
				return new Defline(casava18Matcher.group(1),null);
			}
			Matcher beginSeqMatcher =FastqUtil.SEQ_DEFLINE_PATTERN.matcher(fastqDefline);
	        if(!beginSeqMatcher.find()){
	            throw new IllegalStateException("invalid fastq file, could not parse seq id from "+ fastqDefline);
	        }
	        return new Defline(beginSeqMatcher.group(1), beginSeqMatcher.group(3));
		}
		public String getId() {
			return id;
		}

		public String getComment() {
			return comment;
		}

		
	}
}
