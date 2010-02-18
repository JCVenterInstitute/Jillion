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
