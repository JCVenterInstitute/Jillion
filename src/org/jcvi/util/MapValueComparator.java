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

package org.jcvi.util;

import java.util.Comparator;
import java.util.Map;

/**
 * {@code MapValueComparator} is a {@link Comparator}
 * that allows sorting a Map's keys based on the values mapped
 * to the keys.
 * @author dkatzel
 *
 *
 */
public class MapValueComparator<K,V extends Comparable> implements Comparator<K> {

    private final Map<K, V> map;
    
    
    
    /**
     * @param map
     */
    public MapValueComparator(Map<K, V> map) {
        this.map = map;
    }



    /**
    * {@inheritDoc}
    */
    @SuppressWarnings("unchecked")
    @Override
    public int compare(K o1, K o2) {
        
        return map.get(o1).compareTo(map.get(o2));
    }

}
