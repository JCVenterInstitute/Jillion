/*
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

/**
 * <code>Level3DeltaStrategy</code> performs three rounds of deltas.
 * @author dkatzel
 */
public class Level3DeltaStrategy extends AbstractLevelDeltaStrategy {
    /**
     * Constructor.
     * @param valueSize reference of the valueSizeStrategy to use to read/write
     * buffers.
     */
    public Level3DeltaStrategy(ValueSizeStrategy valueSize) {
        super(valueSize);
    }
   /**
    * 
   * {@inheritDoc}
    */
    @Override
    protected int computeDelta(int u1, int u2, int u3) {
        return 3*u1 - 3*u2 + u3;
    }

}
