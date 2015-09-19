/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 2, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.util.ObjectsUtil;
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
