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

package org.jcvi.common.core.assembly.coverage.slice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


import org.jcvi.common.core.Direction;
import org.jcvi.common.core.assembly.util.slice.DefaultSliceElement;
import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.SliceElement;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

import org.junit.Test;
import static org.junit.Assert.*;
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
    }
    @Test
    public void manyElements(){
        List<SliceElement> elements = new ArrayList<SliceElement>();
        elements.add(
                new DefaultSliceElement("name1", Nucleotide.Adenine, PhredQuality.valueOf(42), Direction.FORWARD));
    
        elements.add(
                new DefaultSliceElement("name2", Nucleotide.Cytosine, PhredQuality.valueOf(2), Direction.REVERSE));
    
        sut = createNew(elements);
        assertEquals(elements.size(),sut.getCoverageDepth());
        for(SliceElement element : elements){
            assertEquals(element, sut.getSliceElement(element.getId()));
        }
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
