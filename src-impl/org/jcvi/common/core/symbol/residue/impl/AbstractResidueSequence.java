package org.jcvi.common.core.symbol.residue.impl;

import org.jcvi.common.core.symbol.residue.Residue;
import org.jcvi.common.core.symbol.residue.ResidueSequence;

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
public abstract class AbstractResidueSequence<R extends Residue> implements ResidueSequence<R>{

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
        	throw new IndexOutOfBoundsException("ungapped offset " + ungappedOffset + "extends beyond sequence length "+ length);
        }
        return gappedOffset;
    }
    
    private void checkPositiveOffset(int offset){
    	if(offset<0){
    		throw new IndexOutOfBoundsException("offset can not be negative");
    	}
    }
	
}
