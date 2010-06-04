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

package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.trim.AbstractContigTrimmer;
import org.jcvi.assembly.trim.PlacedReadTrimmer;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class AceContigTrimmer extends AbstractContigTrimmer<AcePlacedRead, AceContig> {

    private DefaultAceContig.Builder builder;
    private NucleotideEncodedGlyphs oldConsensus;
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
        if(currentRanges.isEmpty()){
            throw new IllegalStateException(String.format("contig %s is empty after trimming", builder.getContigId()));
        }
        if(currentRanges.size()>1){
            throw new IllegalStateException(String.format("contig %s broke into multiple pieces during trimming", builder.getContigId()));
        }
        Range contigRange = currentRanges.get(0);
        int ungappedStart =oldConsensus.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)contigRange.getStart());
        int ungappedEnd =oldConsensus.convertGappedValidRangeIndexToUngappedValidRangeIndex((int)contigRange.getEnd());
        builder.setContigId(String.format("%s_%d_%d",builder.getContigId(),ungappedStart+1, ungappedEnd+1));
        return builder.build();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected void trimRead(AcePlacedRead placedRead, long trimmedOffset,
            String trimmedBasecalls, Range newValidRange) {
        builder.addRead(placedRead.getId(), trimmedBasecalls, (int)trimmedOffset, 
                placedRead.getSequenceDirection(), newValidRange, placedRead.getPhdInfo());
        final Range sequenceRange = Range.buildRangeOfLength(trimmedOffset,trimmedBasecalls.length());
        
        if(currentRanges ==null){
            currentRanges = new ArrayList<Range>();
        }
        currentRanges.add(sequenceRange); 
        currentRanges = Range.mergeRanges(currentRanges);
        
    }

}
