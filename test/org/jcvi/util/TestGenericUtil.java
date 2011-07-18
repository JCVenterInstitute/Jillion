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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.util.GenericUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestGenericUtil {

    @Test(expected = NullPointerException.class)
    public void toArrayNullCollectionShouldThrowNullPointerException(){
        GenericUtil.toArray(Object[].class, null);
    }
    
    @Test(expected = NullPointerException.class)
    public void toArrayNullClassTypeShouldThrowNullPointerException(){
        GenericUtil.toArray(null, Collections.emptyList());
    }
    @Test
    public void toArrayEmptyCollectionShouldReturnArrayWith0Length(){
        Object[] array = GenericUtil.toArray(Object[].class, Collections.emptyList());
        assertEquals(0,array.length);
    }
    @Test
    public void toArray(){
        String[] array = GenericUtil.toArray(String[].class, Arrays.asList("one","two","three"));
        assertEquals(3,array.length);
        assertEquals("one", array[0]);
        assertEquals("two", array[1]);
        assertEquals("three", array[2]);
    }
    @Test
    public void toArrayWhichHasSubclasses(){
        List<Number> numbers = new ArrayList<Number>();
        numbers.addAll(Arrays.asList(1,2,3));
        numbers.add(4.0);
        Number[] array = GenericUtil.toArray(Number[].class, numbers );
        assertEquals(4,array.length);
        assertEquals(1, array[0]);
        assertEquals(2, array[1]);
        assertEquals(3, array[2]);
        assertEquals(4.0, array[3]);
    }
}
