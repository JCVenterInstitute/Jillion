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
