/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.sequence.SequenceDirection;

public final class DefaultContigFileParser  {
    private static final String CR = "\n";
    private static final Pattern NEW_CONTIG_PATTERN = Pattern.compile("##(\\S+).+");
    private static final Pattern NEW_READ_PATTERN = Pattern.compile("#(\\S+)\\((-?\\d+)\\)\\s+\\[(.*)\\].+\\{(-?\\d+) (-?\\d+)\\}.+");
    
    
    public static void parseInputStream(InputStream inputStream, ContigFileVisitor visitor){
        Scanner scanner = createScannerFor(inputStream);
        while(scanner.hasNextLine()){
           String line = scanner.nextLine();
           fireVisitLine(line, visitor);
           Matcher newContigMatcher =NEW_CONTIG_PATTERN.matcher(line);
           if(newContigMatcher.matches()){
               handleNewContig(newContigMatcher, visitor);
           }
           else{
               Matcher newSequenceMatcher =NEW_READ_PATTERN.matcher(line);
               if(newSequenceMatcher.matches()){
                   fireVisitNewRead(newSequenceMatcher, visitor);
               }
               else{
                   fireVisitBasecallsLine(line, visitor);
               }
           }
        }
        visitor.visitEndOfFile();
   }

    private static Scanner createScannerFor(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter(CR);
        return scanner;
    }

    private static void handleNewContig(Matcher newContigMatcher,
            ContigFileVisitor visitor) {
        final String contigId = parseContigId(newContigMatcher);
           visitor.visitNewContig(contigId);
    }

    private static void fireVisitLine(String line, ContigFileVisitor visitor) {
        visitor.visitLine(line+CR);
    }

    private static String parseContigId(Matcher newContigMatcher) {
        final String contigId = newContigMatcher.group(1);
        return contigId;
    }

    private static void fireVisitBasecallsLine(String line,
            ContigFileVisitor visitor) {
        visitor.visitBasecallsLine(line);
    }

    private static void fireVisitNewRead(Matcher newSequenceMatcher,  ContigFileVisitor visitor) {
           String seqId = newSequenceMatcher.group(1);
           int offset = Integer.parseInt(newSequenceMatcher.group(2));
           SequenceDirection dir= parseComplimentedFlag(newSequenceMatcher)?SequenceDirection.REVERSE: SequenceDirection.FORWARD;
           Range validRange = parseValidRange(newSequenceMatcher, dir);
           
           visitor.visitNewRead(seqId, offset, validRange, dir);
    }

    private static boolean parseComplimentedFlag(Matcher newSequenceMatcher) {
        return !newSequenceMatcher.group(3).isEmpty();
    }

    private static Range parseValidRange(Matcher newSequenceMatcher,
            SequenceDirection dir) {
            int left = Integer.parseInt(newSequenceMatcher.group(4));
           int right = Integer.parseInt(newSequenceMatcher.group(5));
           Range validRange;
           if(dir == SequenceDirection.REVERSE){
               validRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,right, left);
           }
           else{
               validRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,left, right);
           }
        return validRange;
    }
    
    private DefaultContigFileParser(){}
    

}
