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
/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestEmptyIterator {

    Iterator<String> sut = IteratorUtil.createEmptyIterator();
    @Test
    public void removeDoesNothing(){
        sut.remove();
    }
    
    @Test
    public void hasNextReturnsFalse(){
        assertFalse(sut.hasNext());
    }
    
    @Test
    public void nextThrowsNoSuchElementException(){
        try{
            sut.next();
            fail("should throw no such element exception");
        }catch(NoSuchElementException e){
            assertEquals("no elements in empty iterator", e.getMessage());
        }
    }
}
