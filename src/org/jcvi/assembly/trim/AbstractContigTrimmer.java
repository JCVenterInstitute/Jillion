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

package org.jcvi.assembly.trim;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigTrimmer<P extends PlacedRead, C extends Contig<P>> implements ContigTrimmer<P,C> {

    private final List<PlacedReadTrimmer<P,C>> trimmers = new ArrayList<PlacedReadTrimmer<P,C>>();
    
    
    /**
     * @param trimmers
     */
    public AbstractContigTrimmer(List<PlacedReadTrimmer<P, C>> trimmers) {
        this.trimmers.addAll(trimmers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public C trimContig(C contig,CoverageMap<CoverageRegion<P>> coverageMap) throws TrimmerException{
        
        initializeTrimmers(contig,coverageMap);
        beginTrimmingContig(contig);
        for(P placedRead : contig.getPlacedReads()){
            Range oldValidRange = placedRead.getValidRange();
            
            Range newTrimRange = computeNewTrimRangeFor(placedRead);
            //skip reads that are completely trimmed
            if(newTrimRange.isEmpty()){
                continue;
            }
            long newOffset = placedRead.convertValidRangeIndexToReferenceIndex((int)newTrimRange.getStart());
            
            final NucleotideEncodedGlyphs originalGappedValidBases = placedRead.getEncodedGlyphs();
            final List<NucleotideGlyph> trimedBasecalls = originalGappedValidBases.decode(newTrimRange);
            String trimmedBases = NucleotideGlyph.convertToString(trimedBasecalls);
            long ungappedLength = new DefaultNucleotideEncodedGlyphs(trimedBasecalls).getUngappedLength();
            
            
            final Range ungappedNewValidRange;
            if(placedRead.getSequenceDirection()==SequenceDirection.FORWARD){
                int numberOfGapsTrimmedOff= originalGappedValidBases.computeNumberOfInclusiveGapsInGappedValidRangeUntil((int)newTrimRange.getStart());
                ungappedNewValidRange = Range.buildRangeOfLength(oldValidRange.getStart()+ newTrimRange.getStart()-numberOfGapsTrimmedOff, ungappedLength).convertRange(CoordinateSystem.RESIDUE_BASED);
                
            }else{
                int numberOfGapsTrimmedOffLeft = originalGappedValidBases.computeNumberOfInclusiveGapsInGappedValidRangeUntil((int)newTrimRange.getStart());
                long numberOfBasesTrimmedOffLeft = newTrimRange.getStart()-numberOfGapsTrimmedOffLeft;
                
                long numberOfBasesTrimmedOffRight = originalGappedValidBases.getUngappedLength() -ungappedLength-numberOfBasesTrimmedOffLeft;
                ungappedNewValidRange = Range.buildRange(oldValidRange.getStart()+numberOfBasesTrimmedOffRight, oldValidRange.getEnd()- numberOfBasesTrimmedOffLeft).convertRange(CoordinateSystem.RESIDUE_BASED);    
            }
            
            trimRead(placedRead, newOffset, trimmedBases,ungappedNewValidRange);
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
     * @return
     */
    protected abstract  C buildNewContig();

    /**
     * @param placedRead
     * @param newValidRange
     */
    protected abstract void trimRead(P placedRead, long trimmedOffset, String trimmedBasecalls,Range newValidRange);

    /**
     * @param contig
     */
    protected abstract void beginTrimmingContig(C contig);

    /**
     * @param placedRead
     * @param coverageMap
     * @return
     */
    private Range computeNewTrimRangeFor(P placedRead) {
        Range currentValidRange = Range.buildRangeOfLength(0,placedRead.getLength());
        for(PlacedReadTrimmer<P,C> trimmer : trimmers){
            currentValidRange =trimmer.trimRead(placedRead, currentValidRange);
        }
        return currentValidRange;
    }

}
