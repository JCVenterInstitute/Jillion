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

package org.jcvi.jillion.plate;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.plate.Well;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestWellFactories {

    Well sut = Well.create("A01");
    
    @Test(expected = NullPointerException.class)
    public void nullNameShouldThrowNPE(){
        Well.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void outOfIndexColumnShouldThrowIllegalArgumentException(){
        Well.create("A25");
    }
    @Test(expected = IllegalArgumentException.class)
    public void outOfIndexRowShouldThrowIllegalArgumentException(){
        Well.create("Z01");
    }
    
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void sameReferenceShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValueShouldBeEqual(){
        Well same = Well.create("A01");
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
}
