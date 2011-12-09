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

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageRegion;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
/**
 * {@code AssemblyUtil} is a utility class for working
 * with {@link PlacedRead}s and gapped {@link NucleotideSequence}.
 * @author dkatzel
 */
public final class AssemblyUtil {

    private AssemblyUtil(){}
    /**
     * Create a List of {@link Nucleotide}s that corresponds to the gapped full range
     * (untrimmed, uncomplimented, gapped) version of the given PlacedRead.
     * This method is equivalent to 
     * {@link #buildGappedComplimentedFullRangeBases(NucleotideSequence, Direction, Range, List)
     * buildGappedComplimentedFullRangeBases(placedRead.getEncodedGlyphs(), placedRead.getSequenceDirection(), placedRead.getValidRange(), ungappedUncomplimentedFullRangeBases)}
     * @param placedRead the read to work on.
     * @param ungappedUncomplimentedFullRangeBases the ungapped uncomplimented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @return a new List of {@link Nucleotide}s of the gapped, untrimmed uncomplimented
     * basecalls of the given read.
     * @see #buildGappedComplimentedFullRangeBases(NucleotideSequence, Direction, Range, List)
     */
    public static List<Nucleotide> buildGappedComplimentedFullRangeBases(PlacedRead placedRead, List<Nucleotide> ungappedUncomplimentedFullRangeBases){
        Direction dir =placedRead.getDirection();
        Range validRange = placedRead.getValidRange();
        if(dir==Direction.REVERSE){
            validRange = AssemblyUtil.reverseComplimentValidRange(
                    validRange,
                    ungappedUncomplimentedFullRangeBases.size());
        }
        return buildGappedComplimentedFullRangeBases(placedRead.getNucleotideSequence(),
               placedRead.getDirection(), validRange,
               ungappedUncomplimentedFullRangeBases);
    }
    /**
     * Create a List of {@link Nucleotide}s that corresponds to the gapped full range
     * (untrimmed, uncomplimented, gapped) version of the given sequence.
     * @param gappedValidRangeSequence the {@link NucleotideSequence} that gapped
     * sequence that only contains the trimmed portion of the read that is used
     * in the contig in the assembly.
     * @param dir the direction of the read in the Assembly.
     * @param validRange the ungapped version of the valid range.
     * @param ungappedUncomplimentedFullRangeBases the ungapped uncomplimented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @return a new List of {@link Nucleotide}s of the gapped, untrimmed uncomplimented
     * basecalls of the given read.
     */
    public static List<Nucleotide> buildGappedComplimentedFullRangeBases(
            NucleotideSequence gappedValidRangeSequence, Direction dir, Range validRange,
            List<Nucleotide> ungappedUncomplimentedFullRangeBases){
        List<Nucleotide> ungappedFullRangeComplimented;
        if(dir == Direction.REVERSE){
            ungappedFullRangeComplimented = Nucleotides.reverseCompliment(ungappedUncomplimentedFullRangeBases);
        }
        else{
            ungappedFullRangeComplimented = ungappedUncomplimentedFullRangeBases;
        }
        NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(ungappedFullRangeComplimented.size());

        for(int i=0; i< validRange.getStart(); i++ ){
            builder.append(ungappedFullRangeComplimented.get(i));
        }
        builder.append(gappedValidRangeSequence);
        for(int i=(int)validRange.getEnd()+1; i< ungappedFullRangeComplimented.size(); i++ ){
            builder.append(ungappedFullRangeComplimented.get(i));
        }
     
        return builder.asList();
    }
    
    /**
     * Reverse Compliment the given validRange with regards to its fullLength.
     * @param validRange the valid Range to reverseCompliment.
     * @param fullLength the full length of the untrimmed basecalls.
     * @return a new Range that corresponds to the reverse complimented valid range.
     * @throws IllegalArgumentException if valid range is larger than fullLength
     * @throws NullPointerException if validRange is null.
     */
    public static Range reverseComplimentValidRange(Range validRange, long fullLength){
        if(validRange ==null){
            throw new NullPointerException("valid range can not be null");
        }
        if(fullLength < validRange.size()){
            throw new IllegalArgumentException(
                    String.format("valid range  %s is larger than fullLength %d", validRange, fullLength));
        }
        long newStart = fullLength - validRange.getEnd()-1;
        long newEnd = fullLength - validRange.getStart()-1;
        return Range.buildRange(newStart, newEnd).convertRange(validRange.getCoordinateSystem());
    }
    /**
     * Convert the given gapped valid range offset of a given read into its
     * corresponding ungapped full length (untrimmed) equivalent.
     * @param placedRead the read
     * @param ungappedFullLength the ungapped full length of the untrimmed (raw) read.
     * @param gappedOffset the gapped offset to convert into an ungapped full range offset
     * @return the ungapped full range offset as a positive int.
     */
    public static  int convertToUngappedFullRangeOffset(PlacedRead placedRead, int ungappedFullLength,int gappedOffset) {
        Range validRange = placedRead.getValidRange();
        return convertToUngappedFullRangeOffset(placedRead, ungappedFullLength,
                gappedOffset, validRange);
    }
    public static  int convertToUngappedFullRangeOffset(PlacedRead placedRead, int gappedOffset) {
        Range validRange = placedRead.getValidRange();
        return convertToUngappedFullRangeOffset(placedRead, placedRead.getUngappedFullLength(),
                gappedOffset, validRange);
    }
    
    private static int convertToUngappedFullRangeOffset(PlacedRead placedRead,
            int fullLength, int gappedOffset, Range validRange) {
       
        
        NucleotideSequence nucleotideSequence = placedRead.getNucleotideSequence();
        if(placedRead.getDirection() == Direction.REVERSE){
            int ungappedOffset=nucleotideSequence.getUngappedOffsetFor(gappedOffset);
            int numberOfLeadingBasesTrimmed = fullLength-1 - (int)validRange.getEnd();
            return numberOfLeadingBasesTrimmed + ungappedOffset;
        }        
        int ungappedValidRangeIndex =  nucleotideSequence.getUngappedOffsetFor(gappedOffset);
        return ungappedValidRangeIndex + (int)validRange.getStart();
    }
   
    /**
     * Get the first non-gap {@link Nucleotide} from the left side of the given
     * gappedReadIndex on the given encoded glyphs.  If the given base is not a gap, 
     * then that is the value returned.
     * @param gappedNucleotides the gapped nucleotides to search 
     * @param gappedReadIndex the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code <= gappedReadIndex};
     * may be negative if the sequence starts with gaps.
     */
    public static int getLeftFlankingNonGapIndex(NucleotideSequence gappedNucleotides, int gappedReadIndex) {
        if(gappedReadIndex< 0){
            return gappedReadIndex;
        }
        if(gappedNucleotides.isGap(gappedReadIndex)){
            return getLeftFlankingNonGapIndex(gappedNucleotides,gappedReadIndex-1);
        }
        
        return gappedReadIndex;
    }
    
    
    /**
     * Get the first non-gap {@link Nucleotide} from the right side of the given
     * gappedOffset on the given encoded glyphs.  If the given base is not a gap, 
     * then that is the value returned.
     * @param sequence the gapped {@link NucleotideSequence} to search 
     * @param gappedOffset the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code >= gappedReadIndex}
     */
    public static int getRightFlankingNonGapIndex(NucleotideSequence sequence, int gappedOffset) {
        if(gappedOffset > sequence.getLength() -1){
            return gappedOffset;
        }
        if(sequence.isGap(gappedOffset)){
            return getRightFlankingNonGapIndex(sequence,gappedOffset+1);
        }
        return gappedOffset;
    }
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link NucleotideSequence} and gapped {@link Range}.
     * @param gappedSequence the gapped {@link NucleotideSequence} needed
     * to compute the ungapped coordinates from.  The resulting
     * ungapped range will be in the same coordinate system that the
     * input gapped range is in.
     * @param gappedRange the Range of gapped coordinates.
     * @return a new Range in the same coordinate system as the input
     * range; never null.
     * @throws NullPointerException if either argument is null.
     */
    public static Range toUngappedRange(final NucleotideSequence gappedSequence,
            Range gappedRange) {
        if(gappedSequence ==null){
            throw new NullPointerException("gapped sequence can not be null");
        }
        if(gappedRange ==null){
            throw new NullPointerException("gappedFeatureValidRange can not be null");
        }
        return Range.buildRange(
                gappedSequence.getUngappedOffsetFor((int)gappedRange.getStart()),
                gappedSequence.getUngappedOffsetFor((int)gappedRange.getEnd())
                ).convertRange(gappedRange.getCoordinateSystem());
        
    }
    /**
     * Create a coverage map in <strong>ungapped consensus coordinate space</strong>
     * of the given contig.
     * @param <PR> the type of {@link PlacedRead}s used in the contig.
     * @param <C> the type of {@link Contig}
     * @param contig the contig to create an ungapped coverage map for.
     * @return a new {@link CoverageMap} but where the coordinates in the coverage map
     * refer to ungapped coordinates instead of gapped coordinates.
     */
    public static <PR extends PlacedRead,C extends Contig<PR>> CoverageMap<CoverageRegion<PR>> 
    buildUngappedCoverageMap(C contig){
        return buildUngappedCoverageMap(contig.getConsensus(), contig.getPlacedReads());
    }
    /**
     * Create a coverage map in <strong>ungapped consensus coordinate space</strong>
     * of the given reads aligned to the given consensus.
     * @param <PR> the type of {@link PlacedRead} used.
     * @param consensus the gapped consensus the reads aligned to.
     * @param reads the reads to generate a coverage map for.
     * @return a new {@link CoverageMap} but where the coordinates in the coverage map
     * refer to ungapped coordinates instead of gapped coordinates.
     * 
     */
    public static <PR extends PlacedRead> CoverageMap<CoverageRegion<PR>> 
    buildUngappedCoverageMap(NucleotideSequence consensus, Collection<PR> reads){
        
        CoverageMap<CoverageRegion<PR>> gappedCoverageMap =DefaultCoverageMap.buildCoverageMap(reads);
        return createUngappedCoverageMap(consensus, gappedCoverageMap);
    }
    
    
    private static <PR extends PlacedRead,C extends Contig<PR>, T extends CoverageRegion<PR>> CoverageMap<CoverageRegion<PR>> createUngappedCoverageMap(
            NucleotideSequence consensus, CoverageMap<T> gappedCoverageMap) {
        List<CoverageRegion<PR>> ungappedCoverageRegions = new ArrayList<CoverageRegion<PR>>();
        for(T gappedCoverageRegion : gappedCoverageMap){
            Range gappedRange = gappedCoverageRegion.asRange();
            Range ungappedRange = AssemblyUtil.toUngappedRange(consensus,gappedRange);
            List<PR> reads = new ArrayList<PR>();
            for(PR read : gappedCoverageRegion){
                reads.add(read);
            }
            
            ungappedCoverageRegions.add(
                    new DefaultCoverageRegion.Builder<PR>(ungappedRange.getStart(),reads)
                                .end(ungappedRange.getEnd())
                                .build());
        }
        
        return  new DefaultCoverageMap<PR, CoverageRegion<PR>>(ungappedCoverageRegions);
    }
}
