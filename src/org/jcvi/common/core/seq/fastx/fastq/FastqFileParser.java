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
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;
import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.seq.fastx.FastXFileVisitor.EndOfBodyReturnCode;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
/**
 * {@code FastqFileParser} parses FASTQ encoded files
 * regardless of how the qualities are encoded.
 * @author dkatzel
 *
 */
public class FastqFileParser {
    
	private static final Pattern CASAVA_1_8_DEFLINE_PATTERN = Pattern.compile("^@(\\S+\\s+\\d:[N|Y]:\\d+:\\S+)\\s*$");
    public static void parse(File fastQFile, FastqFileVisitor visitor ) throws IOException{
        InputStream in = new FileInputStream(fastQFile);
        try{
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    public static void parse(InputStream in, FastqFileVisitor visitor ) throws IOException{
    	if(in ==null){
    		throw new NullPointerException("input stream can not be null");
    	}
    	TextLineParser parser = new TextLineParser(new BufferedInputStream(in));
		
        parse(visitor, parser);
    }
	private static void parse(FastqFileVisitor visitor, TextLineParser parser) {
		visitor.visitFile();
        boolean keepParsing=true;
        try{
	        while(keepParsing && parser.hasNextLine()){
				keepParsing =parseSingleRecord(visitor, parser);
	        }
        }catch(IOException e){
        	throw new IllegalStateException("error reading fastq file",e);
        }
        visitor.visitEndOfFile();
	}
	
	private static boolean parseSingleRecord(FastqFileVisitor visitor, TextLineParser parser) throws IOException{
		String seqLine = parser.nextLine();
        String basecalls = parser.nextLine();
        String qualLine = parser.nextLine();
        String qualities = parser.nextLine();
        visitor.visitLine(seqLine);
        Defline defline = Defline.parse(seqLine);
        FastXFileVisitor.DeflineReturnCode deflineRet= visitor.visitDefline(defline.getId(), defline.getComment());
        if(deflineRet ==null){
        	throw new IllegalStateException("defline return value can not be null");
        }
        if(deflineRet==FastXFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD){
            handleVisitBody(visitor, basecalls, qualLine, qualities);
        }else{
            visitLines(visitor, basecalls, qualLine, qualities);
        }
        EndOfBodyReturnCode endOfBodyRet = visitor.visitEndOfBody();
        if(endOfBodyRet ==null){
        	throw new IllegalStateException("end of body return value can not be null");
        }
		return endOfBodyRet==EndOfBodyReturnCode.KEEP_PARSING;
	}
	private static void visitLines(FastqFileVisitor visitor, String basecalls,
			String qualLine, String qualities) {
		visitor.visitLine(basecalls);
		visitor.visitLine(qualLine);
		visitor.visitLine(qualities);
	}
	private static void handleVisitBody(FastqFileVisitor visitor,
			String basecalls, String qualLine, String qualities) {
		visitor.visitLine(basecalls);
		NucleotideSequence encodedNucleotides = new NucleotideSequenceBuilder(basecalls.substring(0, basecalls.length()-1)).build();
		visitor.visitNucleotides(encodedNucleotides);
		visitor.visitLine(qualLine);
		Matcher beginQualityMatcher =FastqUtil.QUAL_DEFLINE_PATTERN.matcher(qualLine);
		if(!beginQualityMatcher.find()){ 
		    throw new IllegalStateException("invalid fastq file, could not parse qual id from "+ qualLine);
		}
		visitor.visitLine(qualities);
		String encodedQualities = qualities.endsWith("\n")?qualities.substring(0, qualities.length()-1) : qualities;
		visitor.visitEncodedQualities(encodedQualities);
	}
	
	private static class Defline{
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
