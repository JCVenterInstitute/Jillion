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
package org.jcvi.fasta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import org.jcvi.io.IOUtil;
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
     * Parse the given InputStream of Fasta data and call the appropriate
     * visitXXX methods on the given visitor.
     * @param in the Inputstream of Fasta data to parse.
     * @param visitor the visitor to call the visit methods on.
     * @throws NullPointerException if inputstream or visitor are null.
     */
    public static void parseFasta(InputStream in, FastaVisitor visitor){
        Scanner scanner = new Scanner(in).useDelimiter("\n");
        visitor.visitFile();
        String currentId=null;
        String currentComment=null;
        StringBuilder currentBody=null;
        try{
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                final String lineWithCR = line+"\n";
                visitor.visitLine(lineWithCR);
                if(line.startsWith(">")){
                    if(currentBody!=null){
                        visitor.visitRecord(currentId, currentComment, currentBody.toString());
                        currentBody = null;
                    }
                    visitor.visitDefline(line);
                    currentId = SequenceFastaRecordUtil.parseIdentifierFromIdLine(line);
                    currentComment = SequenceFastaRecordUtil.parseCommentFromIdLine(line);
                    
                }
                else{
                    visitor.visitBodyLine(line);
                    if(currentBody ==null){
                        currentBody= new StringBuilder();
                    }
                    currentBody.append(lineWithCR);
                }
            }
            if(currentBody!=null){
                visitor.visitRecord(currentId, currentComment, currentBody.toString());
                currentBody = null;
            }
        }
        finally{
            scanner.close();
        }
        visitor.visitEndOfFile();
    }
    

    
}
