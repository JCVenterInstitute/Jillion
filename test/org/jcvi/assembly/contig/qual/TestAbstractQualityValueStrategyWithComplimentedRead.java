/*
 * Created on Mar 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.sequence.SequenceDirection;



public class TestAbstractQualityValueStrategyWithComplimentedRead extends TestAbstractQualityValueStrategy{
    
    private int convertToFullRangeComplimentedIndex(int index){
        int switchedValidLeft = LENGTH - (int)validRange.getEnd();
        int distanceFromLeftValidRange = index+switchedValidLeft;
        return LENGTH - distanceFromLeftValidRange;
    }

    @Override
    protected int complimentIfNeeded(int fullRangeIndex){
        return convertToFullRangeComplimentedIndex(fullRangeIndex);
    }
    @Override
    protected SequenceDirection getSequenceDirection() {
        return SequenceDirection.REVERSE;
    }
}
