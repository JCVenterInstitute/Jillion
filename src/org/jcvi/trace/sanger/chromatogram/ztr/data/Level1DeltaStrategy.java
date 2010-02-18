/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

/**
 * <code>Level1DeltaStrategy</code> performs only one round of delta.
 * @author dkatzel
 */
public class Level1DeltaStrategy extends AbstractLevelDeltaStrategy {
    /**
     * Constructor.
     * @param valueSize reference of the valueSizeStrategy to use to read/write
     * buffers.
     */
    public Level1DeltaStrategy(ValueSizeStrategy valueSize) {
        super(valueSize);
    }
    /**
     * returns the previous  delta value.
     */
    @Override
    protected int computeDelta(int u1, int u2, int u3) {
        return u1;
    }

}
