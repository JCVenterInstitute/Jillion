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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestMapValueComparator {

    @Test
    public void sortByValues(){
        Map<String, Integer> weights = new HashMap<String, Integer>();
        weights.put("Moe", 123);
        weights.put("Curly",300);
        weights.put("Larry",150);
        
        Map<String, Integer> sorted = new TreeMap<String, Integer>(MapValueComparator.create(weights));
        sorted.putAll(weights);
        Set<String> expected = createExpectedOrder("Moe","Larry","Curly");
        assertEquals(expected, sorted.keySet());
    }
    
    private Set<String> createExpectedOrder(String...strings){
        Set<String> expected = new LinkedHashSet<String>();
        for(String s : strings){
            expected.add(s);
        }
        return expected;
    }
    @Test
    public void sortWithDuplicatesShouldSortDupsByKey(){
        Map<String, Integer> heights = new HashMap<String, Integer>();
        heights.put("Alice", 123);
        heights.put("Bob",100);
        heights.put("Dan",120);
        heights.put("Carrol",120);       
        heights.put("Ernie",110);
        
        Map<String, Integer> sorted = new TreeMap<String, Integer>(MapValueComparator.create(heights));
        sorted.putAll(heights);
        Set<String> expected = createExpectedOrder("Bob","Ernie","Carrol","Dan","Alice");
        assertEquals(expected, sorted.keySet());
    }
}
