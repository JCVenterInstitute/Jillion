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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.frg;

import static org.junit.Assert.*;

import org.jcvi.common.core.testUtil.TestUtil;
import org.jcvi.jillion.trace.frg.DefaultLibrary;
import org.jcvi.jillion.trace.frg.Distance;
import org.jcvi.jillion.trace.frg.Library;
import org.jcvi.jillion.trace.frg.MateOrientation;
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
