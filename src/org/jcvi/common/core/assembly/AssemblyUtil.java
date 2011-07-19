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
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageRegion;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
/**
 * {@code AssemblyUtil} is a utility class for working
 * with {@link PlacedRead}s and gapped {@link NucleotideSequence}.
 * @author dkatzel
 *
 *
 */
public final class AssemblyUtil {

    private AssemblyUtil(){}
    /**
     * Create a List of {@link NucleotideGlyph}s that corresponds to the gapped full range
     * (untrimmed, uncomplimented, gapped) version of the given PlacedRead.
     * This method is equivalent to 
     * {@link #buildGappedComplimentedFullRangeBases(NucleotideSequence, SequenceDirection, Range, List)
     * buildGappedComplimentedFullRangeBases(placedRead.getEncodedGlyphs(), placedRead.getSequenceDirection(), placedRead.getValidRange(), ungappedUncomplimentedFullRangeBases)}
     * @param <R> The PlacedRead Type.
     * @param placedRead the read to work on.
     * @param ungappedUncomplimentedFullRangeBases the ungapped uncomplimented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @return a new List of {@link NucleotideGlyph}s of the gapped, untrimmed uncomplimented
     * basecalls of the given read.
     * @see #buildGappedComplimentedFullRangeBases(NucleotideSequence, SequenceDirection, Range, List)
     */
    public static <R extends PlacedRead> List<NucleotideGlyph> buildGappedComplimentedFullRangeBases(R placedRead, List<NucleotideGlyph> ungappedUncomplimentedFullRangeBases){
       return buildGappedComplimentedFullRangeBases(placedRead.getEncodedGlyphs(), placedRead.getSequenceDirection(), placedRead.getValidRange(), ungappedUncomplimentedFullRangeBases);
    }
    /**
     * Create a List of {@link NucleotideGlyph}s that corresponds to the gapped full range
     * (untrimmed, uncomplimented, gapped) version of the given sequence.
     * @param gappedValidRange the {@link Range} that corresponds to the gapped
     * valid range of the read currently in an Assembly.
     * @param dir the direction of the read in the Assembly.
     * @param validRange the ungapped version of the valid range.
     * @param ungappedUncomplimentedFullRangeBases the ungapped uncomplimented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @return a new List of {@link NucleotideGlyph}s of the gapped, untrimmed uncomplimented
     * basecalls of the given read.
     */
    public static List<NucleotideGlyph> buildGappedComplimentedFullRangeBases(
            NucleotideSequence gappedValidRange, SequenceDirection dir, Range validRange, List<NucleotideGlyph> ungappedUncomplimentedFullRangeBases){
        List<NucleotideGlyph> fullRangeComplimented;
        if(dir == SequenceDirection.REVERSE){
            fullRangeComplimented = NucleotideGlyph.reverseCompliment(ungappedUncomplimentedFullRangeBases);
        }
        else{
            fullRangeComplimented = ungappedUncomplimentedFullRangeBases;
        }
        List<NucleotideGlyph> gappedComplimentedFullRange = new ArrayList<NucleotideGlyph>();
        for(int i=0; i< validRange.getStart(); i++ ){
            gappedComplimentedFullRange.add(fullRangeComplimented.get(i));
        }
        gappedComplimentedFullRange.addAll(gappedValidRange.decode());
        for(int i=(int)validRange.getEnd()+1; i< fullRangeComplimented.size(); i++){
            gappedComplimentedFullRange.add(fullRangeComplimented.get(i));
        }
        return gappedComplimentedFullRange;
    }
    
    /**
     * Reverse Compliment the given validRange with regards to its fullLength.
     * @param validRange the valid Range to reverseCompliment.
     * @param fullLength the full length of the untrimmed basecalls.
     * @return a new Range that corresponds to the reverse complimented valid range.
     */
    public static Range reverseComplimentValidRange(Range validRange, long fullLength){
        if(fullLength < validRange.size()){
            throw new IllegalArgumentException(
                    String.format("valid range  %s is larger than fullLength %d", validRange, fullLength));
        }
        long newStart = fullLength - validRange.getEnd()-1;
        long newEnd = fullLength - validRange.getStart()-1;
        return Range.buildRange(newStart, newEnd).convertRange(validRange.getCoordinateSystem());
    }
    /**
     * Convert the given gapped valid range index of a given read into its
     * corresponding ungapped full length (untrimmed) equivalent.
     * @param <R> The PlacedRead Type.
     * @param placedRead the read
     * @param fullLength
     * @param gappedIndex
     * @return
     */
    public static <R extends PlacedRead> int convertToUngappedFullRangeIndex(R placedRead, int fullLength,int gappedIndex) {
        Range validRange = placedRead.getValidRange();
        return convertToUngappedFullRangeIndex(placedRead, fullLength,
                gappedIndex, validRange);
    }


    
    public static <R extends PlacedRead> int convertToUngappedFullRangeIndex(R placedRead,
            int fullLength, int gappedIndex, Range validRange) {
        int ungappedValidRangeIndex = convertToUngappedValidRangeIndex(placedRead, gappedIndex);        
        if(placedRead.getSequenceDirection() == SequenceDirection.REVERSE){
            validRange = Range.buildRange(fullLength - placedRead.getValidRange().getEnd(), 
                                                    fullLength - placedRead.getValidRange().getStart());
            int distanceFromLeft=  ungappedValidRangeIndex + (int)validRange.getStart();
            return fullLength - distanceFromLeft;
            
        }        
        int distanceFromLeft=  ungappedValidRangeIndex + (int)validRange.getStart();
        
        return distanceFromLeft;
    }



    public static int convertToUngappedValidRangeIndex(PlacedRead placedRead, int gappedIndex) {
       return placedRead.getEncodedGlyphs().convertGappedValidRangeIndexToUngappedValidRangeIndex(gappedIndex);
    }
    
    public static boolean afterEndOfRead(int rightFlankingNonGapIndex,
            NucleotideSequence placedRead) {
        return rightFlankingNonGapIndex> placedRead.getLength()-1;
    }

    public static boolean isAGap(NucleotideSequence glyphs, int gappedReadIndex) {
        return glyphs.isGap(gappedReadIndex);
    }
    /**
     * Get the first non-gap {@link NucleotideGlyph} from the left side of the given
     * gappedReadIndex on the given encoded glyphs.  If the given base is not a gap, 
     * then that is the value returned.
     * @param gappedNucleotides the gapped nucleotides to search 
     * @param gappedReadIndex the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code <= gappedReadIndex}
     */
    public static int getLeftFlankingNonGapIndex(NucleotideSequence gappedNucleotides, int gappedReadIndex) {
        if(beforeStartOfRead(gappedReadIndex)){
            return gappedReadIndex;
        }
        if(isAGap(gappedNucleotides, gappedReadIndex)){
            return getLeftFlankingNonGapIndex(gappedNucleotides,gappedReadIndex-1);
        }
        
        return gappedReadIndex;
    }
    public static boolean beforeStartOfRead(int gappedReadIndex) {
        return gappedReadIndex<0;
    }
    
    /**
     * Get the first non-gap {@link NucleotideGlyph} from the right side of the given
     * gappedReadIndex on the given encoded glyphs.  If the given base is not a gap, 
     * then that is the value returned.
     * @param gappedNucleotides the gapped nucleotides to search 
     * @param gappedReadIndex the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code >= gappedReadIndex}
     */
    public static int getRightFlankingNonGapIndex(NucleotideSequence placedRead, int gappedReadIndex) {
        if(afterEndOfRead(gappedReadIndex, placedRead)){
            return gappedReadIndex;
        }
        if(isAGap(placedRead, gappedReadIndex)){
            return getRightFlankingNonGapIndex(placedRead,gappedReadIndex+1);
        }
        return gappedReadIndex;
    }
    
    public static Range convertGappedRangeIntoUngappedRange(final NucleotideSequence encodedGlyphs,
            Range gappedFeatureValidRange) {
        int ungappedLeft = encodedGlyphs.convertGappedValidRangeIndexToUngappedValidRangeIndex(
                AssemblyUtil.getRightFlankingNonGapIndex(encodedGlyphs, (int)gappedFeatureValidRange.getStart()));
        int ungappedRight = encodedGlyphs.convertGappedValidRangeIndexToUngappedValidRangeIndex(
                AssemblyUtil.getLeftFlankingNonGapIndex(encodedGlyphs, (int)gappedFeatureValidRange.getEnd()));
        Range ungappedRange = Range.buildRange(ungappedLeft, ungappedRight);
        return ungappedRange;
    }
    
    public static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> DefaultCoverageMap<PR,T> 
        buildCoverageMap(C contig){
        return DefaultCoverageMap.buildCoverageMap(contig.getPlacedReads());
    }
    
    public static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> DefaultCoverageMap<PR,T> 
    buildUngappedCoverageMap(C contig){
        return buildUngappedCoverageMap(contig.getConsensus(), contig.getPlacedReads());
    }
    
    public static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> DefaultCoverageMap<PR,T> 
    buildUngappedCoverageMap(NucleotideSequence consensus, Collection<PR> reads){
        
        CoverageMap<T> gappedCoverageMap =DefaultCoverageMap.buildCoverageMap(reads);
        return createUngappedCoverageMap(consensus, gappedCoverageMap);
    }
    private static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> DefaultCoverageMap<PR, T> createUngappedCoverageMap(
            NucleotideSequence consensus, CoverageMap<T> gappedCoverageMap) {
        List<CoverageRegion<PR>> ungappedCoverageRegions = new ArrayList<CoverageRegion<PR>>();
        for(T gappedCoverageRegion : gappedCoverageMap){
            Range gappedRange = gappedCoverageRegion.asRange();
            Range ungappedRange = consensus.convertGappedValidRangeToUngappedValidRange(gappedRange);
            List<PR> reads = new ArrayList<PR>();
            for(PR read : gappedCoverageRegion){
                reads.add(read);
            }
            
            ungappedCoverageRegions.add(
                    new DefaultCoverageRegion.Builder<PR>(ungappedRange.getStart(),reads)
                                .end(ungappedRange.getEnd())
                                .build());
        }
        
        return (DefaultCoverageMap<PR, T>) new DefaultCoverageMap<PR, CoverageRegion<PR>>(ungappedCoverageRegions);
    }
}
