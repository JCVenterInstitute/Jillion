/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
