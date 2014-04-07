/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.jillion.core.util.MapValueComparator;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestMapValueComparator {

	private final Comparator<Integer> comparator = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.intValue() - o2.intValue();
		}
     				
     };
    Map<String, Integer> weights = new HashMap<String, Integer>();
 
    
    public TestMapValueComparator(){
    	   weights.put("Moe", 123);
    	    weights.put("Curly",300);
    	    weights.put("Larry",150);
    }
    @Test
    public void sortByValuesAscending(){
        
        assertAscending(weights,"Moe","Larry","Curly");
        
    }
    @Test
    public void sortByValuesDescending(){
        assertDescending(weights,"Curly","Larry","Moe");        
    }
    @Test
    public void sortByValuesDescendingUsingCustomComparator(){
        assertDescendingUsingCustomComparator(weights,"Curly","Larry","Moe");        
    }
    @Test
    public void sortByValuesAscendingUsingCustomComparator(){
        
        assertAscendingUsingCustomComparator(weights,"Moe","Larry","Curly");
        
    }
    private void assertDescendingUsingCustomComparator(
			Map<String, Integer> unsorted, String ...expectedOrder) {
    	 Set<String> expected = createExpectedOrder(expectedOrder);
         Map<String, Integer> sorted = MapValueComparator.sortDescending(unsorted, comparator);
         assertEquals("descending",expected, sorted.keySet());
		
	}
	private void assertAscending(Map<String, Integer> unsorted, String...expectedOrder){
        Set<String> expected = createExpectedOrder(expectedOrder);
        Map<String, Integer> sorted = MapValueComparator.sortAscending(unsorted);
        assertEquals("ascending",expected, sorted.keySet());
    }
	private void assertAscendingUsingCustomComparator(Map<String, Integer> unsorted, String...expectedOrder){
        Set<String> expected = createExpectedOrder(expectedOrder);
        Map<String, Integer> sorted = MapValueComparator.sortAscending(unsorted,comparator);
        assertEquals("ascending",expected, sorted.keySet());
    }
    private void assertDescending(Map<String, Integer> unsorted, String...expectedOrder){
        Set<String> expected = createExpectedOrder(expectedOrder);
        Map<String, Integer> sorted = MapValueComparator.sortDescending(unsorted);
        assertEquals("descending",expected, sorted.keySet());
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

        
        assertAscending(heights,"Bob","Ernie","Carrol","Dan","Alice");
        assertDescending(heights,"Alice","Dan","Carrol","Ernie","Bob");
    }

    
    @Test
    public void emptyUnsortedMapShouldReturnEmptySortedMap(){
        Map<String,Integer> emptyMap = Collections.emptyMap();
        assertTrue(MapValueComparator.sortAscending(emptyMap).isEmpty());
        assertTrue(MapValueComparator.sortDescending(emptyMap).isEmpty());
    }
}
