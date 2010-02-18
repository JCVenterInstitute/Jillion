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
/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractIdParser<T> {
    private IdParser<T> sut;
    @Before
    public void setup(){
        sut = createNewIdParser();
    }
    
    protected abstract IdParser<T> createNewIdParser();
    protected abstract T getValidIdAsCorrectType();
    protected abstract String getValidIdAsString();
    protected abstract String getInvalidId();
    
    @Test
    public void validNumber(){
        final String validId = getValidIdAsString();
        final T asCorrectType = getValidIdAsCorrectType();
        assertTrue(sut.isValidId(validId));
        
        assertEquals(asCorrectType,sut.parseIdFrom(validId));
    }
    
    @Test(expected = NumberFormatException.class)
    public void invalidIdShouldThrowNumberFormatExceptionOnparse(){
           
        final String notAnId = getInvalidId();
        assertFalse(sut.isValidId(notAnId));
        sut.parseIdFrom(notAnId);
      
    }     
}
