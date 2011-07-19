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
package org.jcvi;

import org.jcvi.common.core.util.CommonUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCommonUtil {

    @Test
    public void bothNullShouldBeSimilar(){
        assertTrue(CommonUtil.similarTo(null, null));
    }
    @Test
    public void firstNullSecondNotNullShouldNotBeSimilar(){
        assertFalse(CommonUtil.similarTo(null, "not null"));
    }
    @Test
    public void firstAndSecondEqualShouldBeSimilar(){
        String sameObj = "same object";
        assertTrue(CommonUtil.similarTo(sameObj, sameObj));
    }

    @Test
    public void firstAndSecondNotEqualShouldNotBeSimilar(){
        assertFalse(CommonUtil.similarTo("an obj", "a diff obj"));
    }
    
    @Test
    public void bothNull(){
        assertTrue(CommonUtil.bothNull(null, null));
    }
    @Test
    public void bothNullFirstIsNotNull(){
        assertFalse(CommonUtil.bothNull("not null", null));
    }
    @Test
    public void bothNullSecondIsNotNull(){
        assertFalse(CommonUtil.bothNull(null, "not null"));
    }
    
    @Test
    public void bothNullNeitherIsNull(){
        assertFalse(CommonUtil.bothNull("not null", "not null"));
    }
    
    @Test
    public void onlyOneIsNull(){
        assertTrue(CommonUtil.onlyOneIsNull(null, "not null"));
        assertTrue(CommonUtil.onlyOneIsNull("not null", null));
    }
    @Test
    public void onlyOneIsNullBothAreNull(){
        assertFalse(CommonUtil.onlyOneIsNull(null, null));
    }
    
    @Test
    public void onlyOneIsNullNeitherIsNull(){
        assertFalse(CommonUtil.onlyOneIsNull("not null", "not null"));
    }
    
    @Test
    public void canNotBeNullShouldThowNullPointerIfIsNull(){
        String message = "error message to be thrown";
        try{
            CommonUtil.cannotBeNull(null, message);
            fail("should throw NullPointer if is null");
        }catch(NullPointerException e){
            assertEquals(message, e.getMessage());
        }
    }
    @Test
    public void canNotBeNull(){
        CommonUtil.cannotBeNull(new Object(), "message ignored");
      
    }
}
