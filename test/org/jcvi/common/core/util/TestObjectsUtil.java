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
 * Created on Jun 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.util;

import org.jcvi.common.core.util.ObjectsUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestObjectsUtil {

    @Test
    public void bothNullShouldBeSimilar(){
        assertTrue(ObjectsUtil.nullSafeEquals(null, null));
    }
    @Test
    public void firstNullSecondNotNullShouldNotBeSimilar(){
        assertFalse(ObjectsUtil.nullSafeEquals(null, "not null"));
    }
    @Test
    public void firstAndSecondEqualShouldBeSimilar(){
        String sameObj = "same object";
        assertTrue(ObjectsUtil.nullSafeEquals(sameObj, sameObj));
    }

    @Test
    public void firstAndSecondNotEqualShouldNotBeSimilar(){
        assertFalse(ObjectsUtil.nullSafeEquals("an obj", "a diff obj"));
    }
    
    @Test
    public void bothNull(){
        assertTrue(ObjectsUtil.allNull(null, null));
    }
    @Test
    public void bothNullFirstIsNotNull(){
        assertFalse(ObjectsUtil.allNull("not null", null));
    }
    @Test
    public void bothNullSecondIsNotNull(){
        assertFalse(ObjectsUtil.allNull(null, "not null"));
    }
    
    @Test
    public void bothNullNeitherIsNull(){
        assertFalse(ObjectsUtil.allNull("not null", "not null"));
    }
   
    
    @Test
    public void canNotBeNullShouldThowNullPointerIfIsNull(){
        String message = "error message to be thrown";
        try{
            ObjectsUtil.checkNotNull(null, message);
            fail("should throw NullPointer if is null");
        }catch(NullPointerException e){
            assertEquals(message, e.getMessage());
        }
    }
    @Test
    public void canNotBeNull(){
        ObjectsUtil.checkNotNull(new Object(), "message ignored");
      
    }
}
