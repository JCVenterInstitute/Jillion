/*
 * Created on Dec 6, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.util.HashMap;
import java.util.Map;



/**
 *  Implementation of the ZTR Delta32 Data Format.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class Delta32Data extends AbstractDeltaData {
    /**
     * map of all possible strategy implementations.
     */
    private final static Map<Integer, DeltaStrategy> STRATEGY_MAP;
    /**
     * 32-bit delta uses int values.
     */
    private final static ValueSizeStrategy INT_VALUE_STRATEGY = new IntValueSizeStrategy();
    /**
     * creates and populates the STRATEGY_MAP
     */
    static{
        //populate map
        STRATEGY_MAP = new HashMap<Integer, DeltaStrategy>();
        STRATEGY_MAP.put(Integer.valueOf(1), new Level1DeltaStrategy(INT_VALUE_STRATEGY));
        STRATEGY_MAP.put(Integer.valueOf(2), new Level2DeltaStrategy(INT_VALUE_STRATEGY));
        STRATEGY_MAP.put(Integer.valueOf(3), new Level3DeltaStrategy(INT_VALUE_STRATEGY));
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected final DeltaStrategy getDeltaStrategy(int level) {
        return STRATEGY_MAP.get(Integer.valueOf(level));
    }
    /**
     * 2 extra bytes of padding are needed to make 
     * the total length divisible by 4.
     */
    @Override
    protected final int getPaddingSize() {
        return 2;
    }

    
}
