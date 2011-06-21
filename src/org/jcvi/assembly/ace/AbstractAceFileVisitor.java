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
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.ace.consed.ConsedUtil;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractAceFileVisitor implements AceFileVisitor{
    private String currentContigId;
    private String currentReadId;
    private int currentReadGappedFullLength;
    private int currentReadUngappedFullLength;
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

    @Override
    public synchronized void visitHeader(int numberOfContigs, int totalNumberOfReads) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitQualityLine(int qualLeft,
            int qualRight, int alignLeft, int alignRight) {
        throwExceptionIfInitialized();  
        if(qualLeft == -1 && qualRight ==-1){
            skipCurrentRead = true;
            return;
        }
        if((qualRight-qualLeft) <0){
            //invalid converted ace file? 
            skipCurrentRead = true;
            System.err.printf("dropping read %s because it has a negative valid range %d%n", currentReadId,
                    (qualRight-qualLeft));
            return;
        }    
        //dkatzel 4/2011 - There have been cases when qual coords and align coords
        //do not match; usually qual is a sub set of align
        //but occasionally, qual goes beyond the align coords.
        //I guess this happens in a referenced based alignment for
        //reads at the edges when the reads have good quality 
        //beyond the reference.
        //Therefore intersect the qual and align coords
        //to find the region we are interested in
        Range qualityRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
        Range alignmentRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
        Range validRange = qualityRange.intersection(alignmentRange)
                    .convertRange(CoordinateSystem.RESIDUE_BASED);
        
        AssembledFrom assembledFrom =currentAssembledFromMap.get(currentReadId);
        currentOffset = computeReadOffset(assembledFrom, validRange.getLocalStart());            
        int clearLeft;
        int clearRight;
        if(assembledFrom.getSequenceDirection() == SequenceDirection.REVERSE){
            clearLeft = reverseCompliment(currentReadGappedFullLength, validRange.getLocalStart());
            clearRight = reverseCompliment(currentReadGappedFullLength, validRange.getLocalEnd());
            int temp = clearLeft;
            clearLeft = clearRight;
            clearRight = temp;
        }
        else{
            clearLeft = (int)validRange.getLocalStart();
            clearRight = (int)validRange.getLocalEnd();
        }
      //this will set currentValidBasecalls to only be the valid range
        currentValidBases = currentBasecalls.substring(
                        (int)validRange.getStart(), 
                        (int)validRange.getEnd()+1); 
        final int numberOfGaps = getNumberOfGapsIn(currentValidBases);
        final int numberOfFullLengthGaps = getNumberOfGapsIn(currentBasecalls.toString());
        currentReadUngappedFullLength = currentReadGappedFullLength - numberOfFullLengthGaps;
        clearRight -= numberOfGaps;               
        currentClearRange = Range.buildRange(Range.CoordinateSystem.RESIDUE_BASED,clearLeft, clearRight);
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


    private int reverseCompliment(int fullLength, long position) {
        return fullLength - (int)position+1;
    }
    
    private int computeReadOffset(AssembledFrom assembledFrom, long startPosition) {
        return assembledFrom.getStartOffset() + (int)startPosition -2;
    }

    @Override
    public synchronized void visitLine(String line) {
        throwExceptionIfInitialized();
    }

    @Override
    public synchronized void visitReadHeader(String readId, int gappedLength) {
        throwExceptionIfInitialized();
        currentReadId = readId;
        currentReadGappedFullLength = gappedLength;
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
                    currentClearRange ,currentPhdInfo,currentReadUngappedFullLength);
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
     * @param ungappedFullLength the full Length (including invalid range)
     * of the basecalls.
     */
    protected abstract void visitAceRead(String readId, String validBasecalls, 
            int offset, SequenceDirection dir, Range validRange, PhdInfo phdInfo,
            int ungappedFullLength);
    
    
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
            Date creationDate, String data) {
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
    /**
     * {@inheritDoc}
     */
     @Override
     public void visitEndOfContig() {
         
     
     }
}
