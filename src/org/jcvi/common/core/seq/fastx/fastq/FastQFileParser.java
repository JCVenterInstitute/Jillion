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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;


import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.io.TextLineParser;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
/**
 * {@code FastQFileParser} parses FASTQ encoded files
 * regardless of how the qualities are encoded.
 * @author dkatzel
 *
 */
public class FastQFileParser {
    
    public static void parse(File fastQFile, FastQFileVisitor visitor ) throws FileNotFoundException{
        InputStream in = new FileInputStream(fastQFile);
        try{
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    public static void parse(InputStream in, FastQFileVisitor visitor ){
    	if(in ==null){
    		throw new NullPointerException("input stream can not be null");
    	}
    	TextLineParser parser;
		try {
			parser = new TextLineParser(new BufferedInputStream(in));
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new IllegalStateException("error reading file");
			
		}
        visitor.visitFile();
        boolean visitCurrentBlock;
        boolean keepParsing=true;
        try{
	        while(keepParsing && parser.hasNextLine()){
	            String seqLine = parser.nextLine();
	            String basecalls = parser.nextLine();
	            String qualLine = parser.nextLine();
	            String qualities = parser.nextLine();
	            visitor.visitLine(seqLine);
	            Matcher beginSeqMatcher =FastQUtil.SEQ_DEFLINE_PATTERN.matcher(seqLine);
	            if(!beginSeqMatcher.find()){
	                throw new IllegalStateException("invalid fastq file, could not parse seq id from "+ seqLine);
	            }
	            String id = beginSeqMatcher.group(1);
	            String optionalComment =beginSeqMatcher.group(3);
	            visitCurrentBlock = visitor.visitBeginBlock(id, optionalComment);
	            if(visitCurrentBlock){
	                visitor.visitLine(basecalls);
	                NucleotideSequence encodedNucleotides = new DefaultNucleotideSequence(Nucleotides.getNucleotidesFor(basecalls.subSequence(0, basecalls.length()-1)));
	                visitor.visitNucleotides(encodedNucleotides);
	                visitor.visitLine(qualLine);
	                Matcher beginQualityMatcher =FastQUtil.QUAL_DEFLINE_PATTERN.matcher(qualLine);
	                if(!beginQualityMatcher.find()){ 
	                    throw new IllegalStateException("invalid fastq file, could not parse qual id from "+ qualLine);
	                }
	                visitor.visitLine(qualities);
	                String encodedQualities = qualities.endsWith("\n")?qualities.substring(0, qualities.length()-1) : qualities;
	                visitor.visitEncodedQualities(encodedQualities);
	            }else{
	                visitor.visitLine(basecalls);
	                visitor.visitLine(qualLine);
	                visitor.visitLine(qualities);
	            }
	            keepParsing =visitor.visitEndBlock();
	        }
        }catch(IOException e){
        	throw new IllegalStateException("error reading fastq file",e);
        }
        visitor.visitEndOfFile();
    }
}
