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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

public class TestDefaultLibrary {

    String id = "library id";
    MateOrientation orientation = MateOrientation.INNIE;
    MateOrientation differentOrientation = MateOrientation.OUTTIE;
    
    Distance distance = Distance.buildDistance(0, 10);
    Distance differentDistance = Distance.buildDistance(20,60);
    
    Library sut = new DefaultLibrary(id, distance, orientation);
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(distance, sut.getDistance());
        assertEquals(orientation, sut.getMateOrientation());
    }
    
    @Test(expected=NullPointerException.class)
    public void nullIdShouldThrowIllegalArgumentException(){
        new DefaultLibrary(null, distance, orientation);
    }
    
    @Test(expected=NullPointerException.class)
    public void nullDistanceShouldThrowIllegalArgumentException(){
        new DefaultLibrary(id, null, orientation);
    }
    
    @Test(expected=NullPointerException.class)
    public void nullMateOrientationShouldThrowIllegalArgumentException(){
        new DefaultLibrary(id, distance, null);
    }
    @Test
    public void notEqualtoNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualtoDifferentClass(){
        assertFalse(sut.equals("different class"));
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        Library sameValues = new DefaultLibrary(id, distance, orientation);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void onlyIdShouldBeUsedToConsiderEquality(){
        Library hasDifferentDistance = new DefaultLibrary(id, differentDistance, orientation);
        Library hasDifferentOrientation = new DefaultLibrary(id, distance, differentOrientation);
        Library hasDifferentId = new DefaultLibrary("different" +id, distance, orientation);
        
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentDistance);
        TestUtil.assertEqualAndHashcodeSame(sut, hasDifferentOrientation);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentId);
    }
}
