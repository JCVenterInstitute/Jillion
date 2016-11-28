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
package org.jcvi.jillion.internal.core.residue;

import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;

/**
 * {@code AbstractResidueSequence} is an abstract implementation of 
 * {@link ResidueSequence} that implements some of the common methods
 * that are required by {@link ResidueSequence}.  These implementations
 * should be considered default implementations and may be overridden
 * by subclasses if a more efficient method can be used instead.
 * @author dkatzel
 *
 * @param <R> the type of {@link Residue} in this sequence.
 */
public abstract class AbstractResidueSequence<R extends Residue, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R, T>> implements ResidueSequence<R, T, B>{

	@Override
    public long getUngappedLength(){
        return getLength() - getNumberOfGaps();
    }
    @Override
    public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapOffsets()){
            if(gapIndex.intValue() <=gappedValidRangeIndex){
                numberOfGaps++;
            }else{
            	//we've gone past our valid range index
            	//so we can break out of the loop.
            	break;
            }
        }
        return numberOfGaps;
    }
    private int computeNumberOfInclusiveGapsInUngappedValidRangeUntil(int ungappedValidRangeIndex) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapOffsets()){
            //need to account for extra length
        	//due to gaps being added to ungapped index
            if(gapIndex.intValue() <=ungappedValidRangeIndex + numberOfGaps){
                numberOfGaps++;
            }else{
            	//we've gone past our valid range index
            	//so we can break out of the loop.
            	break;
            }
        }
        return numberOfGaps;
    }

   

   
    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(int gappedOffset) {
    	checkPositiveOffset(gappedOffset);
    	long length = getLength();
    	if(gappedOffset> length-1){
        	throw new IndexOutOfBoundsException("gapped offset " + gappedOffset + " extends beyond sequence length "+ length);
        }
        return gappedOffset - getNumberOfGapsUntil(gappedOffset);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(int ungappedOffset) {
    	checkPositiveOffset(ungappedOffset);
    	long length = getLength();
    	
        int gappedOffset= ungappedOffset +computeNumberOfInclusiveGapsInUngappedValidRangeUntil(ungappedOffset);
        if(gappedOffset> length-1){
        	throw new IndexOutOfBoundsException(
        			String.format("ungapped offset %d (gapped offset %d extends beyond sequence length %d", ungappedOffset, gappedOffset, length));
        }
        return gappedOffset;
    }
    
    private void checkPositiveOffset(int offset){
    	if(offset<0){
    		throw new IndexOutOfBoundsException("offset can not be negative");
    	}
    }
	
}
