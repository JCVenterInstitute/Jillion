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

package org.jcvi.common.core.util.iter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.common.core.util.iter.ChainedIterator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestChainedIterator {
    private Iterator<String> sut;
    
    List<String> stooges = Arrays.asList("larry","moe","curly");
    List<String> emptyList = Collections.emptyList();
    List<String> stooges2 = Arrays.asList("shemp","curly-joe","joe besser");
    @Before
    public void setup(){
        sut = ChainedIterator.create(Arrays.asList(
                stooges.iterator(),
                emptyList.iterator(),
                stooges2.iterator())
        );
    }
    
    @Test
    public void whenFirstIteratorFinishedShouldStartIteratingSecond(){
        List<String> expected = new ArrayList<String>();
        expected.addAll(stooges);
        expected.addAll(stooges2);
        assertTrue(sut.hasNext());
        for(int i=0; i< expected.size(); i++){
            assertEquals(expected.get(i), sut.next());
        }
        assertFalse(sut.hasNext());
        try{
            sut.next();
            fail("should throw no such element exception when iterators are empty");
        }catch(NoSuchElementException e){
            //expected
        }
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void removeShouldThrowException(){
        sut.remove();
    }
    
    @Test
    public void emptyIterators(){
        Iterator<String> iter = ChainedIterator.create(Collections.singleton(emptyList.iterator()));
        assertFalse(iter.hasNext());
    }
    
    @Test(expected = NullPointerException.class)
    public void nullParameterInConstructorShouldThrowNPE(){
        ChainedIterator.create(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullIteratorInListWillThrowNPE(){
        ChainedIterator.create(Arrays.asList(
                stooges.iterator(),
                null));
        
        
    }
}
