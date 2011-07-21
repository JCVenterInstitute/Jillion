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
 * Created on Jul 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.testUtil;

import static org.junit.Assert.*;


public final class TestUtil {
    public static void assertEqualAndHashcodeSame(Object obj1, Object obj2) {
        assertEquals(obj1, obj2);
        assertTrue(obj1.hashCode()== obj2.hashCode());

        assertEquals(obj2,obj1);
        assertTrue(obj2.hashCode()== obj1.hashCode());
    }

    public static void assertNotEqualAndHashcodeDifferent(Object obj1,Object obj2) {
        assertFalse(obj1.equals(obj2));
        assertFalse(obj1.hashCode()== obj2.hashCode());

        assertFalse(obj2.equals(obj1));
        assertFalse(obj2.hashCode()== obj1.hashCode());
    }

}
