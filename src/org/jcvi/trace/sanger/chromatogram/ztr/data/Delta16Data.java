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
 * Created on Nov 1, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.util.HashMap;
import java.util.Map;




/**
 * <code>Delta16Data</code> is the concrete implementation
 * of {@link AbstractDeltaData} which encodes the deltas between
 * successive short values.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class Delta16Data extends AbstractDeltaData {
    /**
     * map of all possible strategy implementations.
     */
    private final static Map<Integer, DeltaStrategy> STRATEGY_MAP;
    /**
     * 16-bit delta uses short values.
     */
    private final static ValueSizeStrategy SHORT_SIZE_STRATEGY = new ShortValueSizeStrategy();
    /**
     * creates and populates the STRATEGY_MAP
     */
    static{
        //populate map
        STRATEGY_MAP = new HashMap<Integer, DeltaStrategy>();
        STRATEGY_MAP.put(Integer.valueOf(1), new Level1DeltaStrategy(SHORT_SIZE_STRATEGY));
        STRATEGY_MAP.put(Integer.valueOf(2), new Level2DeltaStrategy(SHORT_SIZE_STRATEGY));
        STRATEGY_MAP.put(Integer.valueOf(3), new Level3DeltaStrategy(SHORT_SIZE_STRATEGY));
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected final DeltaStrategy getDeltaStrategy(int level) {
        return STRATEGY_MAP.get(Integer.valueOf(level));
    }
}
