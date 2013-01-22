/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.slice;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code GapQualityValueStrategies} are {@link QualityValueStrategy}
 * implementations that differ on what the quality value 
 * of a gap will be.
 * @author dkatzel
 *
 *
 */
public enum GapQualityValueStrategies implements QualityValueStrategy{
    /**
     * {@code LOWEST_FLANKING} will find the lowest
     * non-gap quality value that flanks the gap.
     */
    LOWEST_FLANKING{
        
        @Override
        protected PhredQuality getQualityValueIfReadEndsWithGap() {
            return LOWEST_QUALITY;
        }

        @Override
        protected PhredQuality getQualityValueIfReadStartsWithGap() {
            return LOWEST_QUALITY;
        }

        @Override
        protected PhredQuality computeQualityValueForGap(
                int numberOfGapsBetweenFlanks, int ithGapToCompute,
                PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality) {
            if(leftFlankingQuality.compareTo(rightFlankingQuality)<0){
                return leftFlankingQuality;
            }
            return rightFlankingQuality;
        }
    },
    /**
     * {@code ALWAYS_ZERO} will always give a gap a {@link PhredQuality} value 
     * of {@code 0}.
     */
    ALWAYS_ZERO{
        @Override
        protected PhredQuality computeQualityValueForGap(
                int numberOfGapsBetweenFlanks, int ithGapToCompute,
                PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality) {
            return PhredQuality.valueOf(0);
        }

        @Override
        protected PhredQuality getQualityValueIfReadEndsWithGap() {
            return PhredQuality.valueOf(0);
        }

        @Override
        protected PhredQuality getQualityValueIfReadStartsWithGap() {
            return PhredQuality.valueOf(0);
        }   
    }
    ;
    
    private static final PhredQuality LOWEST_QUALITY = PhredQuality.valueOf(1);
    
    @Override
    public PhredQuality getQualityFor(AssembledRead placedRead,
            QualitySequence fullQualities,
            int gappedReadIndex) {
        if(fullQualities ==null){
            throw new NullPointerException("null qualities for "+placedRead);
        }
        final QualitySequence unComplimentedQualities;
        if(placedRead.getDirection()==Direction.REVERSE){
        	unComplimentedQualities = new QualitySequenceBuilder(fullQualities)
        									.reverse()
        									.build();
        }else{
        	unComplimentedQualities = fullQualities;
        }
        final NucleotideSequence sequence = placedRead.getNucleotideSequence();
        if(!sequence.isGap(gappedReadIndex)){
            
            return getQualityForNonGapBase(placedRead, unComplimentedQualities, gappedReadIndex);
        }
        int leftFlankingNonGapIndex = AssemblyUtil.getLeftFlankingNonGapIndex(sequence,gappedReadIndex-1);
        int rightFlankingNonGapIndex = AssemblyUtil.getRightFlankingNonGapIndex(sequence,gappedReadIndex+1);
        
        final PhredQuality qualityOfGap = getQualityValueForGap(leftFlankingNonGapIndex, rightFlankingNonGapIndex, placedRead, unComplimentedQualities,gappedReadIndex);
        
        return qualityOfGap;
    }
    protected abstract PhredQuality getQualityValueIfReadStartsWithGap();
    protected abstract PhredQuality getQualityValueIfReadEndsWithGap();
    
    protected abstract PhredQuality computeQualityValueForGap(int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality);
    
    private PhredQuality getQualityValueForGap(int leftFlankingNonGapIndex,
            int rightFlankingNonGapIndex, AssembledRead placedRead,
            QualitySequence unComplimentedQualities,int indexOfGap) {
        if(leftFlankingNonGapIndex <0){
            return getQualityValueIfReadStartsWithGap();
        }
        if(rightFlankingNonGapIndex> placedRead.getGappedLength()-1){
            return getQualityValueIfReadEndsWithGap();
        }        
        PhredQuality leftFlankingQuality = getQualityForNonGapBase(placedRead, unComplimentedQualities, leftFlankingNonGapIndex);
        PhredQuality rightFlankingQuality = getQualityForNonGapBase(placedRead, unComplimentedQualities, rightFlankingNonGapIndex);
        int ithGapToCompute = indexOfGap - leftFlankingNonGapIndex-1;
        final int numberOfGapsBetweenFlanks = rightFlankingNonGapIndex-leftFlankingNonGapIndex-1;
        return computeQualityValueForGap(numberOfGapsBetweenFlanks, ithGapToCompute, leftFlankingQuality, rightFlankingQuality);
    }
    

    protected PhredQuality getQualityForNonGapBase(AssembledRead placedRead, QualitySequence uncomplementedQualities,
            int gappedReadIndexForNonGapBase) {
        try{
            int ungappedFullRangeIndex = AssemblyUtil.convertToUngappedFullRangeOffset(placedRead, (int)uncomplementedQualities.getLength(),gappedReadIndexForNonGapBase);            
            return uncomplementedQualities.get(ungappedFullRangeIndex);
        }
        catch(Exception e){
            throw new IllegalArgumentException("could not get quality for read " + placedRead +" at gapped index " +gappedReadIndexForNonGapBase,e);
        }
    }
}
