/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import java.util.PrimitiveIterator.OfInt;

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
                PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality) {
            return PhredQuality.valueOf(0);
        }

    }
    ;
    /**
     * Get the entire {@link QualitySequence} for the given gapped 
     * valid Range sequence, the "raw" corresponding quality sequence,
     *  the validRange of this sequence
     * into the "raw" sequence, and the sequence's direction
     * that only includes the valid range portion (provided the
     * {@link AssembledRead#getReadInfo()} ) and gap qualities
     * have been inserted at the appropriate locations.
     * @param validRangeSequence the gapped {@link NucleotideSequence}
     * containing only the valid bases.  This is usually
     * a read's sequence returned by {@link AssembledRead#getNucleotideSequence()}.
     * Can not be null.
     * @param rawQualities the raw {@link QualitySequence} as provided
     * by the sequencing machine. These qualities are "full length"
     * meaning they contain the qualities of the bases that have been
     * trimmed off and are also in the original orientation from the
     * sequence machine.  This means if the read is reverse complemented,
     * then the qualities <strong>will not</strong> be complemented.
     * Can not be null.
     * @param validRange a {@link Range} that explains <strong>in ungapped coordinates</strong>
     * where this valid range sequence "goes" into the raw sequence.
     * @param direction the read's direction which will tell us if the nucleotide
     * sequence and raw quality sequence are in the same orientation or not.
     * @return a new {@link QualitySequence} instance; will never be null.
     * @throws NullPointerException if any parameters are null.
     */
    public QualitySequence getGappedValidRangeQualitySequenceFor(NucleotideSequence validRangeSequence, QualitySequence rawQualities, Range validRange, Direction direction){
    	QualitySequenceBuilder complementedRawQualities = new QualitySequenceBuilder(rawQualities);
    	
    	QualitySequenceBuilder gappedValidRangeQualities = complementedRawQualities.copy().trim(validRange);
    	if(direction == Direction.REVERSE){
    		gappedValidRangeQualities.reverse();
    		complementedRawQualities.reverse();
    	}
    	OfInt gapOffsets=validRangeSequence.gaps().iterator();
		
    	int rawShiftOffset = (int)validRange.getBegin();
    	//TODO we currently don't support any gap value strategy
    	//that interpolates across consecutive gap runs
    	//so we removed those parameters from #computeQualityValueForGap()
    	//they were very expensive to compute
    	//if we want to add it back
    	//a good idea might be to cluster the gapOffsets
    	//into Ranges, then we can compute the left and right ungapped
    	//flanks once and call computeQualityValueForGap() range.length() times
    	while(gapOffsets.hasNext()){
    		int offset = gapOffsets.nextInt();
    		int leftFlank = validRangeSequence.getUngappedOffsetFor(offset);
    		int rightFlank = leftFlank+1;
    		
    		 PhredQuality leftQuality =complementedRawQualities.get(rawShiftOffset+leftFlank);
             PhredQuality rightQuality =complementedRawQualities.get(rawShiftOffset+rightFlank);
             
             PhredQuality gappedQuality = computeQualityValueForGap(leftQuality, 
             		rightQuality);
             gappedValidRangeQualities.insert(offset, gappedQuality);
    	}
    	return gappedValidRangeQualities.build();
    }
    
    /**
     * Get the gapped valid range {@link QualitySequence} for the given read
     * that only includes the valid range portion (provided the
     * {@link AssembledRead#getReadInfo()} ) and gap qualities
     * have been inserted at the appropriate locations.
     * <p>
     * This is a convience method for 
     * {@link #getGappedValidRangeQualitySequenceFor(NucleotideSequence, QualitySequence, Range, Direction)
     * getGappedValidRangeQualitySequenceFor(read.getNucleotideSequence(), 
    			rawQualities, 
    			read.getReadInfo().getValidRange(), 
    			read.getDirection());}.
     * @param read the read, can not be null.
     * @param rawQualities the raw {@link QualitySequence} as provided
     * by the sequencing machine. These qualities are "full length"
     * meaning they contain the qualities of the bases that have been
     * trimmed off and are also in the original orientation from the
     * sequence machine.  This means if the read is reverse complemented,
     * then the qualities <strong>will not</strong> be complemented.
     * Can not be null.
     * @return a new {@link QualitySequence} instance; will never be null.
     * @throws NullPointerException if any parameters are null.
     */
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

   
   
    protected abstract PhredQuality computeQualityValueForGap(PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality);
    
   
    

    protected PhredQuality getQualityForNonGapBase(AssembledRead placedRead, QualitySequence uncomplementedQualities,
            int gappedReadIndexForNonGapBase) {
        int ungappedFullRangeIndex = AssemblyUtil.convertToUngappedFullRangeOffset(placedRead, gappedReadIndexForNonGapBase);            
        
        return uncomplementedQualities.get(ungappedFullRangeIndex);
    }
}
