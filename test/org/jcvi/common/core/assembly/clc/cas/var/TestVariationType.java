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

package org.jcvi.common.core.assembly.clc.cas.var;

import org.jcvi.common.core.assembly.clc.cas.var.Variation;
import org.jcvi.common.core.assembly.clc.cas.var.Variation.Type;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestVariationType {

    @Test
    public void noChange(){
        assertEquals(Type.NO_CHANGE, Variation.Type.getType("NOCHANGE"));
        assertEquals(Type.NO_CHANGE, Variation.Type.getType("nochange"));
        assertEquals(Type.NO_CHANGE, Variation.Type.getType("NoChange"));
    }
    @Test
    public void difference(){
        assertEquals(Type.DIFFERENCE, Variation.Type.getType("DIFFERENCE"));
        assertEquals(Type.DIFFERENCE, Variation.Type.getType("difference"));
        assertEquals(Type.DIFFERENCE, Variation.Type.getType("Difference"));
    }
    @Test
    public void deletion(){
        assertEquals(Type.DELETION, Variation.Type.getType("DELETION"));
        assertEquals(Type.DELETION, Variation.Type.getType("deletion"));
        assertEquals(Type.DELETION, Variation.Type.getType("Deletion"));
    }
    @Test
    public void insert(){
        assertEquals(Type.INSERT, Variation.Type.getType("INSERT"));
        assertEquals(Type.INSERT, Variation.Type.getType("insert"));
        assertEquals(Type.INSERT, Variation.Type.getType("Insert"));
    }
}
