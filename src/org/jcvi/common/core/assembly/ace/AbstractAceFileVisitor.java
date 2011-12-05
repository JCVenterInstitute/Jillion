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
package org.jcvi.common.core.assembly.ace;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
/**
 * {@code AbstractAceFileVisitor} is the main {@link AceFileVisitor}
 * implementation that will interpret the visit method calls
 * to build valid reads and contigs.
 * @author dkatzel
 *
 *
 */
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
    private String currentFullLengthBases;
    private boolean currentContigIsComplimented=false;
    private boolean initialized;
    
    public synchronized boolean isInitialized() {
        return initialized;
    }
    protected final String getCurrentFullLengthBasecalls(){
        return currentFullLengthBases;
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
            Direction dir, int gappedStartOffset) {
        throwExceptionIfInitialized();
        if(readingConsensus){
            readingConsensus=false;
           visitNewContig(currentContigId, 
                   ConsedUtil.convertAceGapsToContigGaps(currentBasecalls.toString()),
                   currentContigIsComplimented);
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
     * @param isComplimented is this contig complimented
     * @see #visitEndOfContig()
     */
    protected abstract void visitNewContig(String contigId, String consensus, boolean isComplimented);

    @Override
    public synchronized void visitConsensusQualities() {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized void visitBaseSegment(Range gappedConsensusRange, String readId) {
        throwExceptionIfInitialized();

    }

    @Override
    public synchronized boolean visitContigHeader(String contigId, int numberOfBases,
            int numberOfReads, int numberOfBaseSegments,
            boolean reverseComplimented) {
        throwExceptionIfInitialized();
        currentContigId = contigId;
        currentAssembledFromMap = new HashMap<String, AssembledFrom>();
        readingConsensus = true;
        currentBasecalls = new StringBuilder();
        currentContigIsComplimented = reverseComplimented;
        return true;
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
        //It might also be possible that the read has been 
        //edited and that could have changed the coordinates.
        //Therefore intersect the qual and align coords
        //to find the region we are interested in
        Range qualityRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, qualLeft,qualRight);
        Range alignmentRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, alignLeft,alignRight);
        Range gappedValidRange = qualityRange.intersection(alignmentRange)
                    .convertRange(CoordinateSystem.RESIDUE_BASED);
        
        AssembledFrom assembledFrom =currentAssembledFromMap.get(currentReadId);
        if(assembledFrom ==null){
            throw new IllegalStateException("unknown read no AF record for "+ currentReadId);
        }
        currentOffset = computeReadOffset(assembledFrom, gappedValidRange.getLocalStart());            
       
        currentFullLengthBases = currentBasecalls.toString();
        NucleotideSequence gappedFullLengthSequence = NucleotideSequenceFactory.createGappy(
                currentFullLengthBases.replaceAll("\\*", "-"));
      //this will set currentValidBasecalls to only be the valid range
        NucleotideSequence gappedValidBases = NucleotideSequenceFactory.createGappy(
                gappedFullLengthSequence.asList(gappedValidRange));
        currentValidBases = gappedValidBases.toString();
        final int numberOfFullLengthGaps = gappedFullLengthSequence.getNumberOfGaps();
        currentReadUngappedFullLength = currentReadGappedFullLength - numberOfFullLengthGaps;
        //dkatzel 2011-11-18
        //It is possible that there are gaps outside of the valid
        //range (maybe from editing the ace in consed?)
        //we need to account for that
        //the one problem is that this could cause minor
        //differences if we then re-write the ace since
        //we will lose the gaps outside of the valid range
        //but that won't affect real assembly data
        //it will only show up if both versions (before and after)
        //of the file were diff'ed.
        int ungappedClearLeft = gappedFullLengthSequence.getUngappedOffsetFor((int)gappedValidRange.getStart());
        int ungappedClearRight = gappedFullLengthSequence.getUngappedOffsetFor((int)gappedValidRange.getEnd());
        Range ungappedValidRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED, ungappedClearLeft+1, ungappedClearRight+1 );
        if(assembledFrom.getSequenceDirection() == Direction.REVERSE){
            ungappedValidRange = AssemblyUtil.reverseComplimentValidRange(ungappedValidRange, currentReadUngappedFullLength);            
        }
        currentClearRange = ungappedValidRange;
        
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
            int offset, Direction dir, Range validRange, PhdInfo phdInfo,
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
        clearTempData();
        initialized = true;
        
    }
    
    private void clearTempData(){
        currentContigId = null;
        currentAssembledFromMap = null;
        currentBasecalls = null;
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
     public boolean visitEndOfContig() {
         return true;
     
     }
}
