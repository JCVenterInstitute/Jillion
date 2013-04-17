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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import java.util.List;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;

/**
 * {@code GapQualityValueStrategies} are different 
 * strategy implementations on how to assign
 * quality values to gaps in a sequence.  Usually,
 * sequence machines create ungapped sequences along 
 * with associated quality scores.  Once a sequence has been 
 * assembled, there might be gaps introduced that won't have a corresponding
 * quality value associated with them.  This class
 * has different implementations that can compute the quality values
 * for these gaps differently.
 * 
 * @author dkatzel
 *
 *
 */
public enum GapQualityValueStrategy{
    /**
     * {@code LOWEST_FLANKING} will find the lowest
     * non-gap quality value that flanks the gap.
     */
    LOWEST_FLANKING{
       

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

    }
    ;
    
 
    public QualitySequence getGappedValidRangeQualitySequenceFor(AssembledRead placedRead,
            QualitySequence fullQualities){
    	QualitySequence ungappedComplementedValidRangeQualities = AssemblyUtil.getUngappedComplementedValidRangeQualities(placedRead, fullQualities);
		QualitySequenceBuilder gappedValidRangeQualityBuilder = new QualitySequenceBuilder(ungappedComplementedValidRangeQualities);

    	ReferenceMappedNucleotideSequence sequence = placedRead.getNucleotideSequence();
		List<Integer> gapOffsets=sequence.getGapOffsets();
		
    	for(Integer gapOffset : gapOffsets){
    		int offset = gapOffset.intValue();
    		int leftFlankingNonGapIndex = sequence.getUngappedOffsetFor(AssemblyUtil.getLeftFlankingNonGapIndex(sequence,offset));
            int rightFlankingNonGapIndex = sequence.getUngappedOffsetFor(AssemblyUtil.getRightFlankingNonGapIndex(sequence,offset));
            
            PhredQuality leftQuality =ungappedComplementedValidRangeQualities.get(leftFlankingNonGapIndex);
            PhredQuality rightQuality =ungappedComplementedValidRangeQualities.get(rightFlankingNonGapIndex);
            
            PhredQuality gappedQuality = computeQualityValueForGap(rightFlankingNonGapIndex - leftFlankingNonGapIndex, 
            		offset -leftFlankingNonGapIndex , 
            		leftQuality, rightQuality);
            gappedValidRangeQualityBuilder.insert(offset, gappedQuality);
    	}
    	return gappedValidRangeQualityBuilder.build();
    	
    }

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
   
    protected abstract PhredQuality computeQualityValueForGap(int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality);
    
    private PhredQuality getQualityValueForGap(int leftFlankingNonGapIndex,
            int rightFlankingNonGapIndex, AssembledRead placedRead,
            QualitySequence unComplimentedQualities,int indexOfGap) {
          
        PhredQuality leftFlankingQuality = getQualityForNonGapBase(placedRead, unComplimentedQualities, leftFlankingNonGapIndex);
        PhredQuality rightFlankingQuality = getQualityForNonGapBase(placedRead, unComplimentedQualities, rightFlankingNonGapIndex);
        int ithGapToCompute = indexOfGap - leftFlankingNonGapIndex-1;
        final int numberOfGapsBetweenFlanks = rightFlankingNonGapIndex-leftFlankingNonGapIndex-1;
        return computeQualityValueForGap(numberOfGapsBetweenFlanks, ithGapToCompute, leftFlankingQuality, rightFlankingQuality);
    }
    

    protected PhredQuality getQualityForNonGapBase(AssembledRead placedRead, QualitySequence uncomplementedQualities,
            int gappedReadIndexForNonGapBase) {
        int ungappedFullRangeIndex = AssemblyUtil.convertToUngappedFullRangeOffset(placedRead, (int)uncomplementedQualities.getLength(),gappedReadIndexForNonGapBase);            
        
        return uncomplementedQualities.get(ungappedFullRangeIndex);
    }
}
