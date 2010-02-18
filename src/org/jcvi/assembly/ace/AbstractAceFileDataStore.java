/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.sequence.SequenceDirection;
/**
 * {@code AbstractAceFileVisitor} is an abstract
 * implementation of {@link AceFileVisitor}
 * that does all the computations required
 * to parse a valid Ace File.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAceFileDataStore extends AbstractAceFileVisitor {
    private String currentContigId;
    private String currentReadId;
    private int currentReadFullLength;
    private Map<String, AssembledFrom> currentAssembledFromMap;
    private boolean readingConsensus=true;
    private StringBuilder currentBasecalls = new StringBuilder();
    private DefaultAceContig.Builder contigBuilder;
    private PhdInfo currentPhdInfo;
    private Range currentClearRange;
    private int currentOffset;
    private String currentValidBases;

    private boolean skipCurrentRead=false;
    /**
     * Visit the given fully constructed AceContig. 
     * @param contig the fully constructed AceContig
     * that was built from an Ace File.
     */
    protected abstract void  visitContig(AceContig contig);
    
    
    
    @Override
    public void visitAssembledFromLine(String readId,
            SequenceDirection dir, int gappedStartOffset) {
        super.visitAssembledFromLine(readId, dir, gappedStartOffset);
        if(readingConsensus){
            readingConsensus=false;
            contigBuilder = new DefaultAceContig.Builder(currentContigId, currentBasecalls.toString());
            
        }
        final AssembledFrom assembledFromObj = new AssembledFrom(readId, gappedStartOffset, dir);
        currentAssembledFromMap.put(readId, assembledFromObj);

    }

    @Override
    public void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        super.visitContigHeader(contigId, numberOfBases, numberOfReads, numberOfBaseSegments, reverseComplimented);
        if(!readingConsensus){
            visitContig(contigBuilder.build());            
        }
        currentContigId = contigId;
        currentAssembledFromMap = new HashMap<String, AssembledFrom>();
        readingConsensus = true;
        currentBasecalls = new StringBuilder();
    }


    @Override
    public void visitQualityLine(int clearLeft,
            int clearRight, int alignLeft, int alignRight) {
        super.visitQualityLine(clearLeft, clearRight, alignLeft, alignRight);
        if(clearLeft == -1 && clearRight ==-1){
            skipCurrentRead = true;
        }
        else{
            int end5 = computeEnd5(alignLeft, clearLeft);
            int end3 = computeEnd3(alignRight, clearRight); 
            AssembledFrom assembledFrom =currentAssembledFromMap.get(currentReadId);
            currentOffset = computeReadOffset(assembledFrom, end5);
            currentValidBases = currentBasecalls.substring(end5-1, end3);  
            int correctedClearLeft;
            int correctedClearRight;
            if(assembledFrom.getSequenceDirection() == SequenceDirection.REVERSE){
                correctedClearLeft = reverseCompliment(currentReadFullLength, clearLeft);
                correctedClearRight = reverseCompliment(currentReadFullLength, clearRight);
                int temp = correctedClearLeft;
                correctedClearLeft = correctedClearRight;
                correctedClearRight = temp;
            }
            else{
                correctedClearLeft = clearLeft;
                correctedClearRight = clearRight;
            }
            final int numberOfGaps = getNumberOfGapsIn(currentValidBases);
            correctedClearRight -= numberOfGaps;
            currentClearRange = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,correctedClearLeft, correctedClearRight);
        }
    }
    
    private int getNumberOfGapsIn(String validBases) {
        int count=0;
        for(int i=0; i< validBases.length(); i++){
            if(validBases.charAt(i) == '*'){
               count++;
            }
        }
        return count;
    }


    private int reverseCompliment(int fullLength, int position) {
        return fullLength - position+1;
    }
    private int computeEnd3(int clearRight, int alignRight) {
        return Math.min(clearRight, alignRight);
    }

    private int computeEnd5(int clearLeft, int alignLeft) {
        return Math.max(clearLeft, alignLeft);
    }
    
    private int computeReadOffset(AssembledFrom assembledFrom, int end5) {
        return assembledFrom.getStartOffset() + end5 -2;
    }

    @Override
    public void visitReadHeader(String readId, int gappedLength) {
        super.visitReadHeader(readId, gappedLength);
        currentReadId = readId;
        currentReadFullLength = gappedLength;
        currentBasecalls = new StringBuilder();
    }

    @Override
    public void visitTraceDescriptionLine(String traceName, String phdName,
            Date date) {
        super.visitTraceDescriptionLine(traceName, phdName, date);
        if(!skipCurrentRead){
            currentPhdInfo =new DefaultPhdInfo(traceName, phdName, date);
            AssembledFrom assembledFrom = currentAssembledFromMap.get(currentReadId);
            contigBuilder.addRead(currentReadId, currentValidBases ,currentOffset, assembledFrom.getSequenceDirection(), 
                    currentClearRange ,currentPhdInfo);
        }
        skipCurrentRead=false;
        
    }

    @Override
    public void visitBasesLine(String bases) {
       super.visitBasesLine(bases);
        currentBasecalls.append(bases.trim());
        
    }

    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        if(contigBuilder !=null){
            visitContig(contigBuilder.build()); 
        }
        
    }

    

}
