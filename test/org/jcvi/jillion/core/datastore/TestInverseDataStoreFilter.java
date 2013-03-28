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
package org.jcvi.jillion.core.datastore;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestInverseDataStoreFilter {

    private DataStoreFilter mock;
    private final String id = "id";
    private DataStoreFilter sut;
    @Before
    public void setup(){
        mock = createMock(DataStoreFilter.class);  
        sut = DataStoreFilters.invertFilter(mock);
    }
    
    @Test
    public void wrappedFilterSaysTrueShouldReturnFalse(){
        expect(mock.accept(id)).andReturn(true);
        replay(mock);
        assertFalse(sut.accept(id));
        verify(mock);
    }
    @Test
    public void wrappedFilterSaysFalseShouldReturnTrue(){
        expect(mock.accept(id)).andReturn(false);
        replay(mock);
        assertTrue(sut.accept(id));
        verify(mock);
    }
}
