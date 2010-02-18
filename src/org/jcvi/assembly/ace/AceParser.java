/*
 * Created on Feb 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
/**
 * @see <a href="http://bozeman.mbt.washington.edu/consed/distributions/README.18.0.txt">ACE FILE FORMAT in Consed manual</a>
 * @author dkatzel
 *
 *
 */
public class AceParser {
    private static final String BASECALL_PATTERN = "^(\\S+)\\s*$";

    private static final Pattern ACE_HEADER_PATTERN = Pattern.compile("^AS\\s+(\\d+)\\s+(\\d+)");
    
    private static final Pattern CONTIG_HEADER_PATTERN = Pattern.compile("^CO\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)");
    private static final Pattern ASSEMBLED_FROM_PATTERN = Pattern.compile("^AF\\s+(\\S+)\\s+(\\w)\\s+(-?\\d+)");
    private static final Pattern READ_HEADER_PATTERN = Pattern.compile("^RD\\s+(\\S+)\\s+(\\d+)");
    private static final Pattern QUALITY_PATTERN = Pattern.compile("^QA\\s+(-?\\d+)\\s+(-?\\d+)\\s+(\\d+)\\s+(\\d+)");
    private static final Pattern TRACE_DESCRIPTION_PATTERN = Pattern.compile("^DS\\s+CHROMAT_FILE:\\s+(\\S+)\\s+PHD_FILE:\\s+(\\S+)\\s+TIME:\\s+(.+\\d\\d\\d\\d)");
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd kk:mm:ss yyyy");

    private Scanner scanner;
    public AceParser(InputStream aceStream){
        scanner = new Scanner(aceStream);       
    }
    
    public List<AceContig> parseContigsFrom(){
        int numberOfContigs = getNumberOfContigs();
        return parseContigs(numberOfContigs);        
    }
    
    private int getNumberOfContigs() {
        final String nextLine = scanner.nextLine();
        Matcher headerMatcher = ACE_HEADER_PATTERN.matcher(nextLine);
        if(matches(headerMatcher)){        
            return Integer.parseInt(headerMatcher.group(1));           
        }
        throw new RuntimeException("error trying to parse ace file, invalid ace format");
    }
    private List<AceContig> parseContigs(int numberOfContigs) {
        List<AceContig> contigs = new ArrayList<AceContig>(numberOfContigs);
        while(moreContigsLeft()){
            String line =scanner.nextLine();                
            Matcher contigMatcher = CONTIG_HEADER_PATTERN.matcher(line);
            if(matches(contigMatcher)){                
                contigs.add(buildContig(contigMatcher));
            }
        }
        return contigs;
    }

    private DefaultAceContig buildContig(Matcher contigMatcher) {
        int numberOfReads = Integer.parseInt(contigMatcher.group(3)); 
        DefaultAceContig.Builder contigBuilder = createContigBuilder(contigMatcher);                
        Map<String, AssembledFrom> assembledFromMap = parseAssembledFromMap(numberOfReads);
        buildContig(numberOfReads, contigBuilder, assembledFromMap);
        return  contigBuilder.build();
    }

    private void buildContig(int numberOfReads,
            DefaultAceContig.Builder contigBuilder,
            Map<String, AssembledFrom> assembledFromMap) {
        while(contigBuilder.numberOfReads() < numberOfReads){
            
            parseReadAndAddToBuilder(contigBuilder, assembledFromMap);       
        }
    }

    private void parseReadAndAddToBuilder(DefaultAceContig.Builder contigBuilder,
            Map<String, AssembledFrom> assembledFromMap) {
        String contigLine = scanner.nextLine();                    
        Matcher readMatcher = READ_HEADER_PATTERN.matcher(contigLine);
        if(matches(readMatcher)){
            String readId = readMatcher.group(1);
            int fullLength = Integer.parseInt(readMatcher.group(2));
            String readBasecalls =parseBasecalls();
            String qualityHeaderLine;
            do{
                qualityHeaderLine = scanner.nextLine();
            }while(qualityHeaderLine.trim().isEmpty());
            Matcher qualityMatcher = QUALITY_PATTERN.matcher(qualityHeaderLine);
            if(matches(qualityMatcher)  && goodQualityRead(qualityMatcher)){
                    addReadToBuilder(contigBuilder, assembledFromMap, readId,
                            fullLength, readBasecalls, qualityMatcher);
            }
            
        }
    }

    private void addReadToBuilder(DefaultAceContig.Builder contigBuilder,
            Map<String, AssembledFrom> assembledFromMap, String readId,
            int fullLength, String readBasecalls, Matcher qualityMatcher) {
        AssembledFrom assembledFrom =assembledFromMap.get(readId);
        
        int clearLeft = Integer.parseInt(qualityMatcher.group(1));
        int clearRight = Integer.parseInt(qualityMatcher.group(2)); 
        int end5 = computeEnd5(qualityMatcher, clearLeft);
        int end3 = computeEnd3(qualityMatcher, clearRight);                               
        int offset = computeReadOffset(assembledFrom, end5);
        
        String validBases = readBasecalls.substring(end5-1, end3);                                
        
        if(assembledFrom.getSequenceDirection() == SequenceDirection.REVERSE){
            clearLeft = reverseCompliment(fullLength, clearLeft);
            clearRight = reverseCompliment(fullLength, clearRight);
            int temp = clearLeft;
            clearLeft = clearRight;
            clearRight = temp;
        }
        final int numberOfGaps = getGapIndexesFrom(validBases).size();
        clearRight -= numberOfGaps;
              
        final Range clearRange = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED, clearLeft, clearRight);
        PhdInfo phdInfo = parsePhdInfo(scanner.nextLine());
        contigBuilder.addRead(readId, validBases,offset, assembledFrom.getSequenceDirection(), 
                clearRange,phdInfo);
    }

    private PhdInfo parsePhdInfo(String phdInfoLine) {
        Matcher matcher =TRACE_DESCRIPTION_PATTERN.matcher(phdInfoLine);
        if(matcher.find()){
            String traceName = matcher.group(1);
            String phdName = matcher.group(2);
            Date date= DATE_TIME_FORMATTER.parseDateTime(matcher.group(3)).toDate();
            return new DefaultPhdInfo(traceName, phdName, date);
        }
        throw new RuntimeException("could not parse phd info from " + phdInfoLine);
        
    }

    private DefaultAceContig.Builder createContigBuilder(Matcher contigMatcher) {
        final String contigId = contigMatcher.group(1);
        String consensus =parseBasecalls();
        DefaultAceContig.Builder contigBuilder = new DefaultAceContig.Builder(contigId, consensus);
        return contigBuilder;
    }

    private int reverseCompliment(int fullLength, int clearLeft) {
        clearLeft = fullLength - clearLeft+1;
        return clearLeft;
    }

    private int computeEnd3(Matcher qualityMatcher, int clearRight) {
        return Math.min(clearRight, Integer.parseInt(qualityMatcher.group(4)));
    }

    private int computeEnd5(Matcher qualityMatcher, int clearLeft) {
        return Math.max(clearLeft, Integer.parseInt(qualityMatcher.group(3)));
    }

    private int computeReadOffset(AssembledFrom assembledFrom, int end5) {
        return assembledFrom.getStartOffset() + end5 -2;
    }

    private boolean goodQualityRead(Matcher qualityMatcher) {
        int clearLeft = Integer.parseInt(qualityMatcher.group(1));
        int clearRight = Integer.parseInt(qualityMatcher.group(2));
        return clearLeft !=-1 || clearRight !=-1;
    }

    private Map<String, AssembledFrom> parseAssembledFromMap(int numberOfReads) {
        Map<String, AssembledFrom> assembledFromMap = new HashMap<String, AssembledFrom>();
        while(assembledFromMap.size()<numberOfReads){
            String contigLine = scanner.nextLine();
            Matcher assembledFromMatcher = ASSEMBLED_FROM_PATTERN.matcher(contigLine);
            if(matches(assembledFromMatcher)){
                createNewAssemblyFromAndAddToMap(assembledFromMap,assembledFromMatcher); 
            }
        }
        return assembledFromMap;
    }

    private boolean matches(Matcher readMatcher) {
        return readMatcher.find();
    }

    private void createNewAssemblyFromAndAddToMap(
            Map<String, AssembledFrom> assembledFromMap,
            Matcher assembledFromMatcher) {
        String name = assembledFromMatcher.group(1);
        SequenceDirection dir = assembledFromMatcher.group(2).equals("C")? SequenceDirection.REVERSE : SequenceDirection.FORWARD;
        
        int fullRangeOffset = Integer.parseInt(assembledFromMatcher.group(3));
        final AssembledFrom assembledFromObj = new AssembledFrom(name, fullRangeOffset, dir);
        assembledFromMap.put(name, assembledFromObj);
    }

    private boolean moreContigsLeft() {
        return scanner.hasNextLine();
    }
    
    private List<Integer> getGapIndexesFrom(String consensus) {
        List<Integer> gaps = new ArrayList<Integer>();
        for(int i=0; i< consensus.length(); i++){
            if(consensus.charAt(i) == '*'){
                gaps.add(Integer.valueOf(i));
            }
        }
        return gaps;
    }
    

    private String parseBasecalls() {
        StringBuilder result = new StringBuilder();
        do{
            String line = scanner.nextLine();
            if(line.matches(BASECALL_PATTERN)){
                result.append(line.trim());
            }
            else{
                break;
            }
        }while(scanner.hasNextLine());
        return result.toString();
    }

}
