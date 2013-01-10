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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor.DeflineReturnCode;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor.EndOfBodyReturnCode;
import org.jcvi.jillion.core.internal.io.TextLineParser;
/**
 * {@code FastaParser} is a utility class
 * to parse Fasta formated files.
 * @author dkatzel
 */
public final class FastaFileParser {
	
	private static final Pattern TRAILING_WHITE_SPACE_PATTERN = Pattern.compile("\\s+$");
	
    private static final Pattern DEFLINE_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
	private static boolean endsWithWhiteSpace(String line){
		Matcher m = TRAILING_WHITE_SPACE_PATTERN.matcher(line);
		return m.find();
	}
    /**
     * private constructor.
     */
    private FastaFileParser(){}
    
    /**
     * Parse the given Fasta file and call the appropriate
     * visitXXX methods on the given visitor.
     * @param fastaFile the Fasta file to parse.
     * @param visitor the visitor to call the visit methods on.
     * @throws FileNotFoundException if the given fasta file does not 
     * exist.
     * @throws NullPointerException if fastaFile or visitor are null.
     */
    public static void parse(File fastaFile, FastaFileVisitor visitor) throws FileNotFoundException{
        if(visitor ==null){
        	throw new NullPointerException("visitor can not be null");
        }
    	InputStream in = new FileInputStream(fastaFile);
        try{
            parse(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    
    /**
     * Parse the given InputStream of Fasta data and call the appropriate
     * visitXXX methods on the given visitor.
     * @param in the Inputstream of Fasta data to parse.
     * @param visitor the visitor to call the visit methods on.
     * @throws NullPointerException if inputstream or visitor are null.
     */    
    public static void parse(InputStream in, FastaFileVisitor visitor){
    	
    	if(visitor ==null){
        	throw new NullPointerException("visitor can not be null");
        }
    	try {
    	    ParserState parserState = new ParserState(in);           
            visitor.visitFile();
            try{
                while(parserState.keepParsing()){
                   parserState = SectionHandler.handleNextSection(parserState, visitor);
                }
                parserState.visitFinalRecord(visitor);
            }finally{
                IOUtil.closeAndIgnoreErrors(parserState);
            }
            visitor.visitEndOfFile();
        } catch (IOException e) {
            throw new IllegalStateException("error reading file",e);
        }
    }
    
    private static class ParserState implements Closeable{
        private boolean keepParsing=true;
        private final TextLineParser parser;
        private boolean done;
        private boolean skipCurrentRecord;
        private boolean seenFirstDefline=false;
        ParserState(InputStream in) throws IOException{
            if(in ==null){
                throw new NullPointerException("input stream can not be null");
            }
            parser = new TextLineParser(new BufferedInputStream(in));
            done= !parser.hasNextLine();
        }
        
        public void seenDefline(){
        	seenFirstDefline=true;
        }
        
        public boolean keepParsing(){
        	return keepParsing && !done;
        }
        public void stopParsing(){
        	keepParsing=false;
        }
        
        public boolean skipCurrentRecord() {
			return skipCurrentRecord;
		}

		public String getNextLine() throws IOException{
            String nextLine= parser.nextLine();
            if(!parser.hasNextLine()){
                done = true;
            }
            return nextLine;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            parser.close();            
        }
        
        public void visitFinalRecord(FastaFileVisitor visitor){
            if(keepParsing && !skipCurrentRecord && seenFirstDefline){
            	EndOfBodyReturnCode ret =visitor.visitEndOfBody();
            	if(ret ==null){
            		throw new IllegalStateException("return from visitDefline can not be null");                	
            	}
            }
        }
        
    }
    /**
     * {@code SectionHandler} has implementations
     * which "handle" each section of a Fasta
     * Record differently.
     * @author dkatzel
     *
     *
     */
    private enum SectionHandler{
        DEFLINE{
            @Override
            boolean canHandle(String line) {
                return line.startsWith(">");
            }
            @Override
            ParserState sectionSpecificHandle(String lineWithCR, ParserState parserState,FastaFileVisitor visitor){
                
                
                String lineWithoutCR = lineWithCR.substring(0, lineWithCR.length()-1);
                if(parserState.seenFirstDefline && !parserState.skipCurrentRecord()){  
                	EndOfBodyReturnCode ret =visitor.visitEndOfBody();
                	if(ret ==null){
                		throw new IllegalStateException("return from visitDefline can not be null");
                	}
                	if(ret == EndOfBodyReturnCode.KEEP_PARSING){
                		parserState.keepParsing = true;
                	}else{
                		parserState.stopParsing();
                	}
                }
                if(parserState.keepParsing()){
	                visitor.visitLine(lineWithCR);
	                Matcher matcher = DEFLINE_LINE_PATTERN.matcher(lineWithoutCR);
	                if(!matcher.find()){
	                	throw new IllegalStateException(
	                			String.format("could not parse defline '%s'", lineWithoutCR));
	                }
	                String id = matcher.group(1);
	                String comment = matcher.group(3);
	                DeflineReturnCode visitDefline = visitor.visitDefline(id,comment);
	                if(visitDefline ==null){
	                	throw new IllegalStateException("return from visitDefline can not be null");
	                }
	                if(visitDefline == DeflineReturnCode.SKIP_CURRENT_RECORD){
	                	parserState.skipCurrentRecord=true;
	                	parserState.keepParsing=true;
	                }else if(visitDefline == DeflineReturnCode.STOP_PARSING){
	                	parserState.stopParsing();
	                }else{
	                	parserState.skipCurrentRecord=false;
	                	parserState.keepParsing=true;
	                }
	                parserState.seenDefline();
                }
                return parserState;
            }
            
        },
        BODY{
            @Override
            boolean canHandle(String line) {
                return true;
            }
            
            @Override
            ParserState sectionSpecificHandle(String lineWithCR, ParserState parserState,FastaFileVisitor visitor){
                
                visitor.visitLine(lineWithCR);
                final String lineWithoutCR ;
                if(endsWithWhiteSpace(lineWithCR)){
                	lineWithoutCR = lineWithCR.trim();
                }else{
                	lineWithoutCR = lineWithCR;
                }
                
                if(!parserState.skipCurrentRecord()){
                	visitor.visitBodyLine(lineWithoutCR);
                }
                return parserState;
            }
            
        };
        
        static ParserState handleNextSection(ParserState parserState,FastaFileVisitor visitor) throws IOException{
            String line =parserState.getNextLine();            
            for(SectionHandler handler : values()){
                if(handler.canHandle(line)){
                    return handler.sectionSpecificHandle(line, parserState, visitor);
                    
                }
            }
            //should never happen since body will accept anything
            throw new IllegalStateException("could not find handler for line " + line);
        }
        
        abstract boolean canHandle(String line);
        abstract ParserState sectionSpecificHandle(String lineWithCR, ParserState parserState,FastaFileVisitor visitor);
        
        
    }
    
}
