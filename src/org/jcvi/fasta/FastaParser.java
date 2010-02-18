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

import java.io.InputStream;
import java.util.Scanner;

public class FastaParser {

    private FastaParser(){}
    
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
