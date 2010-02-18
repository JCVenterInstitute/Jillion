/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

/**
 * <code>Level2DeltaStrategy</code> performs two rounds of deltas.
 * @author dkatzel
 */
public class Level2DeltaStrategy extends AbstractLevelDeltaStrategy {
    /**
     * Constructor.
     * @param valueSize reference of the valueSizeStrategy to use to read/write
     * buffers.
     */
    public Level2DeltaStrategy(ValueSizeStrategy valueSize) {
        super(valueSize);
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected int computeDelta(int u1, int u2, int u3) {
        return  2*u1 - u2;
    }    

}
