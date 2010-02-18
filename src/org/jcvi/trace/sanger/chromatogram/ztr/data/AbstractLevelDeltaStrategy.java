/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;

/**
 * <code>AbstractLevelDeltaStrategy</code> is an abstract implementation
 * of {@link DeltaStrategy}.  concrete implementations of this class
 * will compute a series of rounds of deltas.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractLevelDeltaStrategy implements DeltaStrategy {
    /**
     * reference of the valueSizeStrategy to use to read/write
     * buffers.
     */
    private ValueSizeStrategy valueSizeStrategy;
    /**
     * Constructor.
     * @param valueSize reference of the valueSizeStrategy to use to read/write
     * buffers.
     */
    public AbstractLevelDeltaStrategy(ValueSizeStrategy valueSize){
        this.valueSizeStrategy = valueSize;
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public void unCompress(ByteBuffer compressed, ByteBuffer out) {
        int u1 = 0,u2 = 0,u3=0;
        
        while(compressed.hasRemaining()){
            int value =valueSizeStrategy.getNext(compressed) + computeDelta(u1, u2, u3);
            valueSizeStrategy.put(value, out);
            //update previous values for next round
            u3 = u2;
            u2 = u1;            
            u1 = value;            
        }

    }
    /**
     * Computes the current Delta given the previous 3 delta values. 
     * @param u1 previous delta value.
     * @param u2 the 2nd previous delta value.
     * @param u3 the 3rd previous delta value.
     * @return the current delta value.
     */
    protected abstract int computeDelta(int u1, int u2, int u3);

}
