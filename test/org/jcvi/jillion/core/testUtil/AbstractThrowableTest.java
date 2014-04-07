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
 * Created on Aug 3, 2007
 * Originally created for Flim, 
 * moved to JavaCommon Nov 2008.
 * @author dkatzel
 */
package org.jcvi.jillion.core.testUtil;


import static org.junit.Assert.*;
import org.junit.Test;
/**
 * This is an abstract TestCase
 * which will test the constuctors
 * of an {@link Throwable} object.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractThrowableTest<T extends Throwable>{

    private String message = "test message";
    private Throwable cause = new Exception("testCause");

    protected abstract T createThrowable(String message);

    protected abstract T createThrowable(String message, Throwable cause);

    @Test
    public void testConstructor(){

        Throwable ex = createThrowable(message);
        assertEquals(message,ex.getMessage());
        assertNull(ex.getCause());
    }
    @Test
    public void testConstructorWithCause(){
        Throwable ex = createThrowable(message,cause);
        assertEquals(message,ex.getMessage());
        assertEquals(cause,ex.getCause());
    }

}
