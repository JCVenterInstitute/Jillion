/*
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import static org.junit.Assert.*;

import org.jcvi.Distance;
import org.jcvi.TestUtil;
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
    
    @Test(expected=IllegalArgumentException.class)
    public void nullIdShouldThrowIllegalArgumentException(){
        new DefaultLibrary(null, distance, orientation);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void nullDistanceShouldThrowIllegalArgumentException(){
        new DefaultLibrary(id, null, orientation);
    }
    
    @Test(expected=IllegalArgumentException.class)
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
