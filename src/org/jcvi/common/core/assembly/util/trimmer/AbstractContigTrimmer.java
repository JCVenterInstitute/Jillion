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

package org.jcvi.common.core.assembly.util.trimmer;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code AbstractContigTrimmer} is an abstract implementation
 * of {@link ContigTrimmer} that applies a series
 * of {@link PlacedReadTrimmer}s to reads in a given contig
 * to build a new trimmed version of that contig.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigTrimmer<P extends PlacedRead, C extends Contig<P>> implements ContigTrimmer<P,C> {

    private final List<PlacedReadTrimmer<P,C>> trimmers = new ArrayList<PlacedReadTrimmer<P,C>>();
    
    
    /**
     * Construct a new {@link AbstractContigTrimmer} using the given read trimmers.
     * These read trimmers will be executed in the order they are given so chain trimmers
     * appropriately.
     * @param trimmers a non-null list of read trimmers.
     * @throws NullPointerException if trimmers is null.
     * @throws IllegalArgumentException if trimmers is empty.
     */
    public AbstractContigTrimmer(List<PlacedReadTrimmer<P, C>> trimmers) {
        if(trimmers.isEmpty()){
            throw new IllegalArgumentException("trimmer list can not be null");
        }
        this.trimmers.addAll(trimmers);
    }

    @Override
    public ContigTrimmerResult<P,C> trimContig(C contig) throws TrimmerException {
        return trimContig(contig, DefaultCoverageMap.buildCoverageMap(contig));
    }

    protected ContigTrimmerResult<P,C> trimContig(C contig,CoverageMap<CoverageRegion<P>> coverageMap){
        
        initializeTrimmers(contig,coverageMap);
        beginTrimmingContig(contig);
        CloseableIterator<P> readIter = null;
        try{
        	readIter = contig.getReadIterator();
        	while(readIter.hasNext()){
        		P placedRead = readIter.next();
	            Range oldValidRange = placedRead.getValidRange();
	            
	            Range newTrimRange = computeNewTrimRangeFor(placedRead);
	            //skip reads that are completely trimmed
	            if(newTrimRange.isEmpty()){
	                continue;
	            }
	            long newOffset = placedRead.toReferenceOffset((int)newTrimRange.getBegin());
	            
	            final NucleotideSequence originalGappedValidBases = placedRead.getNucleotideSequence();
	            NucleotideSequence trimmedSequence = new NucleotideSequenceBuilder(originalGappedValidBases.asList(newTrimRange)).build();
				long ungappedLength = trimmedSequence.getUngappedLength();
	            
	            
	            final Range ungappedNewValidRange;
	            if(placedRead.getDirection()==Direction.FORWARD){
	                int numberOfGapsTrimmedOff= originalGappedValidBases.getNumberOfGapsUntil((int)newTrimRange.getBegin());
	                ungappedNewValidRange = Range.createOfLength(oldValidRange.getBegin()+ newTrimRange.getBegin()-numberOfGapsTrimmedOff, ungappedLength);
	                
	            }else{
	                int numberOfGapsTrimmedOffLeft = originalGappedValidBases.getNumberOfGapsUntil((int)newTrimRange.getBegin());
	                long numberOfBasesTrimmedOffLeft = newTrimRange.getBegin()-numberOfGapsTrimmedOffLeft;
	                
	                long numberOfBasesTrimmedOffRight = originalGappedValidBases.getUngappedLength() -ungappedLength-numberOfBasesTrimmedOffLeft;
	                ungappedNewValidRange = Range.create(oldValidRange.getBegin()+numberOfBasesTrimmedOffRight, oldValidRange.getEnd()- numberOfBasesTrimmedOffLeft);    
	            }
	            
	            trimRead(placedRead, newOffset, trimmedSequence.toString(),ungappedNewValidRange);
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(readIter);
        }
        clearTrimmers();
        return buildNewContig();
    }

    /**
     * 
     */
    private void clearTrimmers() {
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            trimmer.clear();
        }
        
    }

    /**
     * @param contig
     * @param coverageMap
     */
    private void initializeTrimmers(C contig,
            CoverageMap<CoverageRegion<P>> coverageMap) {
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            trimmer.initializeContig(contig, coverageMap);
        }
        
    }

    /**
     * Trimming has completed, construct a new Contig instance
     * of the contig with the new trim data.
     * @return a new instance of ContigTrimmerResult;
     * or null if the entire contig was trimmed off.
     */
    protected abstract  ContigTrimmerResult<P,C> buildNewContig();

    /**
     * Trim the given PlacedRead with the given current trimmed
     * offset, basecalls and validRange.  If validRange is empty, then 
     * current the read is completely trimmed off.
     * @param placedRead the original untrimmed placedread
     * @param trimmedOffset the current trimmed start offset of this 
     * read in the contig.
     * @param trimmedBasecalls the current trimmed gapped basecalls.
     * @param newValidRange the current trimmed valid range (ungapped)
     * of the read.
     * 
     */
    protected abstract void trimRead(P placedRead, long trimmedOffset, 
            String trimmedBasecalls,Range newValidRange);

    /**
     * This trimmer will begin trimming the given contig, any calls
     * to {@link #trimRead(PlacedRead, long, String, Range)} will be
     * for reads from this contig until the method {@link #buildNewContig()}.
     * @param contig the original contig that will be trimmed.
     */
    protected abstract void beginTrimmingContig(C contig);

    /**
     * @param placedRead
     * @param coverageMap
     * @return
     */
    private Range computeNewTrimRangeFor(P placedRead) {
        Range currentValidRange = Range.createOfLength(0,placedRead.getGappedLength());
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            currentValidRange =trimmer.trimRead(placedRead, currentValidRange);
        }
        return currentValidRange;
    }

}
