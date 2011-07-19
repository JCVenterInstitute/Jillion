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

package org.jcvi.common.core.assembly.contig.ace;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.trim.AbstractContigTrimmer;
import org.jcvi.assembly.trim.PlacedReadTrimmer;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;

/**
 * {@code AceContigTrimmer} is an Ace implementation of
 * {@link AbstractContigTrimmer} to build valid
 * trimmed {@link AceContig}s.
 * @author dkatzel
 *
 *
 */
public class AceContigTrimmer extends AbstractContigTrimmer<AcePlacedRead, AceContig> {

    private DefaultAceContig.Builder builder;
    private NucleotideSequence oldConsensus;
    private List<Range> currentRanges;
    /**
     * @param trimmers
     */
    public AceContigTrimmer(
            List<PlacedReadTrimmer<AcePlacedRead, AceContig>> trimmers) {
        super(trimmers);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void beginTrimmingContig(AceContig contig) {
        this.oldConsensus = contig.getConsensus();
        builder = new DefaultAceContig.Builder(contig.getId(), NucleotideGlyph.convertToString(oldConsensus.decode()));
        
        currentRanges = null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected AceContig buildNewContig() {
        //currentRanges should now only be 1 range
        if(currentRanges ==null || currentRanges.isEmpty()){
           // throw new IllegalStateException(String.format("contig %s is empty after trimming", builder.getContigId()));
            return null;
        }
        if(currentRanges.size()>1){
            throw new IllegalStateException(String.format("contig %s broke into multiple pieces during trimming", builder.getContigId()));
        }
        Range contigRange = currentRanges.get(0);
        builder.setContigId(createNewContigId(builder.getContigId(),oldConsensus,contigRange));
        return builder.build();
    }

    protected String createNewContigId(String oldContigId, NucleotideSequence oldConsensus, Range newContigRange){
        if(oldConsensus.getLength() == newContigRange.getLength()){
            return oldContigId;
        }
        final int start = (int)newContigRange.getStart();
        int ungappedStart =oldConsensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(AssemblyUtil.getRightFlankingNonGapIndex(oldConsensus, start));
        final int end = (int)newContigRange.getEnd();
        int ungappedEnd =oldConsensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(AssemblyUtil.getLeftFlankingNonGapIndex(oldConsensus, end));
        return String.format("%s_%d_%d",oldContigId,ungappedStart+1, ungappedEnd+1);
    
    }
    /**
    * {@inheritDoc}
    */
    @Override
    protected void trimRead(AcePlacedRead placedRead, long trimmedOffset,
            String trimmedBasecalls, Range newValidRange) {
        builder.addRead(placedRead.getId(), trimmedBasecalls, (int)trimmedOffset, 
                placedRead.getSequenceDirection(), newValidRange, placedRead.getPhdInfo(),
                placedRead.getUngappedFullLength());
        final Range sequenceRange = Range.buildRangeOfLength(trimmedOffset,trimmedBasecalls.length());
        
        if(currentRanges ==null){
            currentRanges = new ArrayList<Range>();
        }
        currentRanges.add(sequenceRange); 
        currentRanges = Range.mergeRanges(currentRanges);
        
    }

}
