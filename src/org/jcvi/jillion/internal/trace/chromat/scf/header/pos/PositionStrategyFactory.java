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
/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.scf.header.pos;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
/**
 * <code>PositionStrategyFactory</code> will return the appropriate
 * {@link PositionStrategy} implementation based on the given input.
 * @author dkatzel
 *
 *
 */
public final class PositionStrategyFactory {
    /**
     * singleton of {@link ShortPositionStrategy} to be returned.
     */
    private static final PositionStrategy SHORT_STRATEGY = new ShortPositionStrategy();
    /**
     * singleton of {@link BytePositionStrategy} to be returned.
     */
    private static final PositionStrategy BYTE_STRATEGY = new BytePositionStrategy();

    private static Map<Integer, PositionStrategy> MAX_SIZE_MAP;
    private static Map<Byte, PositionStrategy> SAMPLE_SIZE_MAP;
    static{
        //populate maps
        //map size map is a sorted set so iterator will return
        // entries in ascending order making finding the
        //correct PositionStrategy easy
        MAX_SIZE_MAP = new TreeMap<Integer, PositionStrategy>();
        MAX_SIZE_MAP.put(BYTE_STRATEGY.getMaxAllowedValue(), BYTE_STRATEGY);
        MAX_SIZE_MAP.put(SHORT_STRATEGY.getMaxAllowedValue(), SHORT_STRATEGY);

        SAMPLE_SIZE_MAP =new HashMap<Byte, PositionStrategy>();
        SAMPLE_SIZE_MAP.put(BYTE_STRATEGY.getSampleSize(), BYTE_STRATEGY);
        SAMPLE_SIZE_MAP.put(SHORT_STRATEGY.getSampleSize(), SHORT_STRATEGY);
    }
    /**
     * private constructor.
     */
    private PositionStrategyFactory(){}
    /**
     * Get the appropriate {@link PositionStrategy} based
     * on the data provided by the given {@link SCFHeader}'s sample size.
     * @param header non-null SCFHeader, must have {@link SCFHeader#getSampleSize()}
     * set.
     * @return a {@link PositionStrategy}; never null.
     */
    public static PositionStrategy getPositionStrategy(SCFHeader header){
        final byte sampleSize = header.getSampleSize();
        PositionStrategy ret =SAMPLE_SIZE_MAP.get(Byte.valueOf(sampleSize));
        if(ret ==null){
            throw new IllegalArgumentException(
                "no Position Strategy implementation available for sample size "
                + sampleSize);
        }
        return ret;
    }
    /**
     * Get the appropriate {@link PositionStrategy} implementation
     * based on max value.
     * @param maxValue the maximum value a position can have.
     * @return a non-null {@link PositionStrategy}.
     * @throws IllegalArgumentException if no position strategy is available
     * for the given max value.
     */
    public static PositionStrategy getPositionStrategy(int maxValue){
        //map size map is a sorted set so iterator returns
        // entries in ascending order
        for(Entry<Integer, PositionStrategy> entry : MAX_SIZE_MAP.entrySet()){
            if(maxValue <= entry.getKey().intValue()){
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException(
                "no Position Strategy implementation available for max value "
                + maxValue);
    }
}
