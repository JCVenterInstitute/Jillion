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
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

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
    
    public QualitySequence getGappedValidRangeQualitySequenceFor(NucleotideSequence validRangeSequence, QualitySequence rawQualities, Range validRange, Direction direction){
    	QualitySequenceBuilder qualities = new QualitySequenceBuilder(rawQualities);
    	
    	QualitySequenceBuilder complementedRawQualities = new QualitySequenceBuilder(rawQualities);
    	if(direction == Direction.REVERSE){
    		qualities.reverse();
    		complementedRawQualities.reverse();
    	}
    	qualities.trim(validRange);
    	
    	List<Integer> gapOffsets=validRangeSequence.getGapOffsets();
		
    	int maxSeqOffset = (int)validRangeSequence.getLength() -1;
    	int rawShiftOffset = (int)validRange.getBegin();
    	for(Integer gapOffset : gapOffsets){
    		int offset = gapOffset.intValue();
    		int leftFlankGappedOffset = Math.max(0, AssemblyUtil.getLeftFlankingNonGapIndex(validRangeSequence,offset));
    		int rightFlankingGappedOffset = AssemblyUtil.getRightFlankingNonGapIndex(validRangeSequence,offset);
			
    		int rightFlankingNonGapIndex;
    		if(rightFlankingGappedOffset > maxSeqOffset){
    			//right flank offset is beyond the valid range
    			//this means the read valid sequence ends with a gap
    			//this is an unlikely edge case that probably can
    			//only happen by rogue software or manual edits
    			rightFlankingNonGapIndex = (int)validRangeSequence.getUngappedLength()+rawShiftOffset;
    		}else{
    			rightFlankingNonGapIndex = validRangeSequence.getUngappedOffsetFor(rightFlankingGappedOffset);
                
    		}
    		
			int leftFlankingNonGapIndex = validRangeSequence.getUngappedOffsetFor(leftFlankGappedOffset);

            PhredQuality leftQuality =complementedRawQualities.get(rawShiftOffset+leftFlankingNonGapIndex);
            PhredQuality rightQuality =complementedRawQualities.get(rawShiftOffset+rightFlankingNonGapIndex);
            
            PhredQuality gappedQuality = computeQualityValueForGap(rightFlankingNonGapIndex - leftFlankingNonGapIndex, 
            		offset -leftFlankingNonGapIndex , 
            		leftQuality, rightQuality);
            qualities.insert(offset, gappedQuality);
    	}
    	
    	return qualities.build();
    }
    public QualitySequence getGappedValidRangeQualitySequenceFor(AssembledRead read,
            QualitySequence rawQualities){
    	if(read ==null){
    		throw new NullPointerException("read can not be null");
    	}
    	if(rawQualities ==null){
    		throw new NullPointerException("qualities can not be null");
    	}
    	return getGappedValidRangeQualitySequenceFor(read.getNucleotideSequence(), 
    			rawQualities, 
    			read.getReadInfo().getValidRange(), 
    			read.getDirection());
    	
    }

   
   
    protected abstract PhredQuality computeQualityValueForGap(int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality);
    
   
    

    protected PhredQuality getQualityForNonGapBase(AssembledRead placedRead, QualitySequence uncomplementedQualities,
            int gappedReadIndexForNonGapBase) {
        int ungappedFullRangeIndex = AssemblyUtil.convertToUngappedFullRangeOffset(placedRead, gappedReadIndexForNonGapBase);            
        
        return uncomplementedQualities.get(ungappedFullRangeIndex);
    }
}
