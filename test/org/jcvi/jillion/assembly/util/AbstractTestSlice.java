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
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.assembly.util.DefaultSliceElement;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSlice {

	Slice sut;
    protected abstract Slice createNew(List<SliceElement> elements);
    
    
    @Test
    public void emptySlice(){
        sut = createNew(Collections.<SliceElement>emptyList());
        assertEquals(0,sut.getCoverageDepth());
        
        assertEquals(createCountMapOfAllZeros(),sut.getNucleotideCounts());
    }
    
    private Map<Nucleotide, Integer> createCountMapOfAllZeros(){
    	Map<Nucleotide,Integer> map = new EnumMap<Nucleotide, Integer>(Nucleotide.class);
    	for(Nucleotide n : Nucleotide.values()){
    		map.put(n, Integer.valueOf(0));
    	}
    	return map;
    }
    
    @Test
    public void oneElement(){
        List<SliceElement> elements = new ArrayList<SliceElement>();
        elements.add(new DefaultSliceElement("name1", Nucleotide.Adenine, PhredQuality.valueOf(42), Direction.FORWARD));
    
        sut = createNew(elements);
        assertEquals(elements.size(),sut.getCoverageDepth());
        for(SliceElement element : elements){
            assertEquals(element, sut.getSliceElement(element.getId()));
        }
        
        Map<Nucleotide, Integer> expectedCountsMap = createCountMapOfAllZeros();
        expectedCountsMap.put(Nucleotide.Adenine, 1);
        Map<Nucleotide, Integer> actualCountsMap =sut.getNucleotideCounts();
        assertEquals(expectedCountsMap, actualCountsMap);
    }
    @Test
    public void manyElements(){
        List<SliceElement> elements = new ArrayList<SliceElement>();
        elements.add(
                new DefaultSliceElement("name1", Nucleotide.Adenine, PhredQuality.valueOf(42), Direction.FORWARD));
    
        elements.add(
                new DefaultSliceElement("name2", Nucleotide.Cytosine, PhredQuality.valueOf(2), Direction.REVERSE));
    
        elements.add(
                new DefaultSliceElement("name3", Nucleotide.Cytosine, PhredQuality.valueOf(2), Direction.REVERSE));
    
        sut = createNew(elements);
        assertEquals(elements.size(),sut.getCoverageDepth());
        for(SliceElement element : elements){
            assertEquals(element, sut.getSliceElement(element.getId()));
        }
        
        Map<Nucleotide, Integer> expectedCountsMap = createCountMapOfAllZeros();
        expectedCountsMap.put(Nucleotide.Adenine, 1);
        expectedCountsMap.put(Nucleotide.Cytosine, 2);
        Map<Nucleotide, Integer> actualCountsMap =sut.getNucleotideCounts();
        assertEquals(expectedCountsMap, actualCountsMap);
    }
    @Test
    public void iterator(){
        List<SliceElement> elements = new ArrayList<SliceElement>();
        elements.add(
                new DefaultSliceElement("name1", Nucleotide.Adenine, PhredQuality.valueOf(42), Direction.FORWARD));
    
        elements.add(
                new DefaultSliceElement("name2", Nucleotide.Cytosine, PhredQuality.valueOf(2), Direction.REVERSE));
    
        sut = createNew(elements);
        Iterator<SliceElement> expected = elements.iterator();
        Iterator<SliceElement> actual = sut.iterator();
        while(expected.hasNext()){
            assertTrue(actual.hasNext());
            assertEquals(expected.next(), actual.next());
        }
        assertFalse(actual.hasNext());
        
    }
}
