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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractAceFileVisitor implements AceFileVisitor{
    private String currentContigId;
    private String currentReadId;
    private int currentReadFullLength;
    private Map<String, AssembledFrom> currentAssembledFromMap;
    private boolean readingConsensus=true;
    private StringBuilder currentBasecalls = new StringBuilder();
    private PhdInfo currentPhdInfo;
    private Range currentClearRange;
    private int currentOffset;
    private String currentValidBases;

    private boolean skipCurrentRead=false;
    
    private boolean initialized;
    
    public synchronized boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public synchronized void visitFile() {
        throwExceptionIfInitialized();
    }

    private synchronized void throwExceptionIfInitialized() {
        if(isInitialized()){
            throw new IllegalStateException("already initialized");
        }
    }
    @Override
    public synchronized void visitAssembledFromLine(String readId,
            SequenceDirection dir, int gappedStartOffset) {
        throwExceptionIfInitialized();
        if(readingConsensus){
            readingConsensus=false;
           visitNewContig(currentContigId, ConsedUtil.convertAceGapsToContigGaps(currentBasecalls.toString()));
        }
        final AssembledFrom assembledFromObj = new AssembledFrom(readId, gappedStartOffset, dir);
        currentAssembledFromMap.put(readId, assembledFromObj);

    }
    /**
     * Begin visiting a new contig in the ace file.  Any visit methods between
     * this call and {@link #visitEndOfContig()} pertain to this contig.
     * @param contigId the ID of the contig being visited.
     * @param consensus the basecalls as a string- NOTE that this has gaps as "*" instead
     * of "-".  
     * @see #visitEndOfContig()
     */
    protected abstract void visitNewContig(String contigId, String consensus);

    @Override
    public synchronized void visitConsensusQualities() {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitBaseSegment(Range gappedConsensusRange, String readId) {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        throwExceptionIfInitialized();
        if(!readingConsensus){
            visitEndOfContig();           
        }
        currentContigId = contigId;
        currentAssembledFromMap = new HashMap<String, AssembledFrom>();
        readingConsensus = true;
        currentBasecalls = new StringBuilder();
    }
    /**
     * The currently contig being visited contains no more data.
     */
    protected abstract void visitEndOfContig();
    @Override
    public synchronized void visitHeader(int numberOfContigs, int totalNumberOfReads) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitQualityLine(int clearLeft,
            int clearRight, int alignLeft, int alignRight) {
        throwExceptionIfInitialized();    
        if(clearLeft == -1 && clearRight ==-1){
            skipCurrentRead = true;
        }
        else{
            int end5 = computeEnd5(alignLeft, clearLeft);
            int end3 = computeEnd3(alignRight, clearRight); 
            AssembledFrom assembledFrom =currentAssembledFromMap.get(currentReadId);
            currentOffset = computeReadOffset(assembledFrom, end5);
            if((end3-end5) <0){
                //invalid converted ace file? 
                //reset end3 to be absolute value of length?
                skipCurrentRead = true;
                System.out.printf("dropping read %s because it has a negative valid range %d%n", currentReadId, (end3-end5));
            }else{
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
    private int computeEnd3(int alignRight, int clearRight) {
        return alignRight;
    }

    private int computeEnd5(int alignLeft, int clearLeft) {
      //  return Math.max(clearLeft, alignLeft);
        return alignLeft;
    }
    
    private int computeReadOffset(AssembledFrom assembledFrom, int end5) {
        return assembledFrom.getStartOffset() + end5 -2;
    }

    @Override
    public synchronized void visitLine(String line) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitReadHeader(String readId, int gappedLength) {
        throwExceptionIfInitialized();
        currentReadId = readId;
        currentReadFullLength = gappedLength;
        currentBasecalls = new StringBuilder();
    }

    @Override
    public synchronized void visitTraceDescriptionLine(String traceName, String phdName,
            Date date) {
        throwExceptionIfInitialized();
        if(!skipCurrentRead){
            currentPhdInfo =new DefaultPhdInfo(traceName, phdName, date);
            AssembledFrom assembledFrom = currentAssembledFromMap.get(currentReadId);
            visitAceRead(currentReadId, currentValidBases ,currentOffset, assembledFrom.getSequenceDirection(), 
                    currentClearRange ,currentPhdInfo);
        }
        skipCurrentRead=false;
    }
    /**
     * Visit an AceRead inside the current contig.  All the math and coordinate conversions
     * have already been computed from the Ace file already.
     * @param readId the id of the read.
     * @param validBasecalls the basecalls as a string- NOTE that this has gaps as "*" instead
     * of "-". 
     * @param offset the 0-based start offset of this read into the contig.
     * @param dir the direction of this read.
     * @param validRange the validRange coordinates of this read's basecalls.
     * @param phdInfo the {@link PhdInfo} for this read (not null).
     */
    protected abstract void visitAceRead(String readId, String validBasecalls, int offset, SequenceDirection dir, Range validRange, PhdInfo phdInfo);
    
    
    @Override
    public synchronized void visitBasesLine(String bases) {
        throwExceptionIfInitialized();
        currentBasecalls.append(bases.trim());
    }

    @Override
    public synchronized void visitEndOfFile() {
        throwExceptionIfInitialized();  
        visitEndOfContig();
        initialized = true;
        
    }

    @Override
    public synchronized void visitBeginConsensusTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitWholeAssemblyTag(String type, String creator,
            Date creationDate, String Data) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusTagComment(String comment) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitConsensusTagData(String data) {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitEndConsensusTag() {
        throwExceptionIfInitialized();
        
    }

    @Override
    public void visitReadTag(String id, String type, String creator,
            long gappedStart, long gappedEnd, Date creationDate,
            boolean isTransient) {
        throwExceptionIfInitialized();
        
    }

}
