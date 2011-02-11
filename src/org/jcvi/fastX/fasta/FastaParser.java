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
package org.jcvi.fastX.fasta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

import org.jcvi.io.IOUtil;
import org.jcvi.io.TextLineParser;
/**
 * {@code FastaParser} is a utility class
 * to parse Fasta formated files.
 * @author dkatzel
 *
 *
 */
public final class FastaParser {
    /**
     * private constructor.
     */
    private FastaParser(){}
    
    /**
     * Parse the given Fasta file and call the appropriate
     * visitXXX methods on the given visitor.
     * @param fastaFile the Fasta file to parse.
     * @param visitor the visitor to call the visit methods on.
     * @throws FileNotFoundException if the given fasta file does not 
     * exist.
     * @throws NullPointerException if fastaFile or visitor are null.
     */
    public static void parseFasta(File fastaFile, FastaVisitor visitor) throws FileNotFoundException{
        InputStream in = new FileInputStream(fastaFile);
        try{
            parseFasta(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given Fasta file and call the appropriate
     * visitXXX methods on the given visitor.  This version
     * will try to acquire this given semaphore
     * before parsing each fasta record in the file
     * (or block until a permit has been released).
     * This is useful if parsing needs to be halted
     * and restarted where the parser left off. 
     * @param fastaFile the Fasta file to parse.
     * @param visitor the visitor to call the visit methods on.
     * @param semaphore the {@link Semaphore} which 
     * this parser will try to acquire when parsing each fasta
     * record.
     * @throws FileNotFoundException if the given fasta file does not 
     * exist.
     * @throws NullPointerException if fastaFile or visitor are null.
     */
    public static void blockingParseFasta(File fastaFile, FastaVisitor visitor) throws FileNotFoundException{
        InputStream in = new FileInputStream(fastaFile);
        try{
            parseFasta(in,visitor);
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
    public static void parseFasta(InputStream in, FastaVisitor visitor){
       // Scanner scanner = new Scanner(in).useDelimiter("\n");
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
        String currentId=null;
        String currentComment=null;
        StringBuilder currentBody=null;
        boolean keepParsing=true;
        try{
            while(keepParsing && parser.hasNextLine()){
            	final String lineWithCR;
				try {
					lineWithCR = parser.nextLine();
				} catch (IOException e) {
					throw new IllegalStateException("error reading file");
				}
                visitor.visitLine(lineWithCR);
                String lineWithoutCR = lineWithCR.substring(0, lineWithCR.length()-1);
                if(lineWithCR.startsWith(">")){
                    if(currentBody!=null){                        
                        keepParsing = visitor.visitRecord(currentId, currentComment, currentBody.toString());
                        currentBody = null;
                    }                    
                    keepParsing = visitor.visitDefline(lineWithoutCR);
                    currentId = SequenceFastaRecordUtil.parseIdentifierFromIdLine(lineWithCR);
                    currentComment = SequenceFastaRecordUtil.parseCommentFromIdLine(lineWithCR);
                    
                }
                else{
                    keepParsing = visitor.visitBodyLine(lineWithoutCR);
                    if(currentBody ==null){
                        currentBody= new StringBuilder();
                    }
                    currentBody.append(lineWithCR);
                }
            }
            if(keepParsing && currentBody!=null){
                visitor.visitRecord(currentId, currentComment, currentBody.toString());
                currentBody = null;
            }
        }
        finally{
            IOUtil.closeAndIgnoreErrors(parser);
        }
        visitor.visitEndOfFile();
    }
    

    
}
