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
