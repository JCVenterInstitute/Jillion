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

package org.jcvi.datastore;

import java.util.Arrays;

import org.jcvi.common.core.datastore.DefaultIncludeDataStoreFilter;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultIncludeDataStoreFilter {

    DefaultIncludeDataStoreFilter sut = new DefaultIncludeDataStoreFilter(Arrays.asList("include_1", "include_2"));
    
    @Test
    public void idIsInIncludeListShouldAccept(){
        assertTrue(sut.accept("include_1"));
        assertTrue(sut.accept("include_2"));
    }
    
    @Test
    public void idIsNotInIncludeListShouldNotAccept(){
        assertFalse(sut.accept("include_3"));
        assertFalse(sut.accept("something completely different"));
    }
}
