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

package org.jcvi.common.io.ansi;

import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static  org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestEscapeCode {

    private final byte code = 123;
    private final EscapeCode sut = new EscapeCode(code);
    @Test
    public void constructor(){        
        assertEquals(code, sut.getCode());
        assertEquals("\u001B["+code+"m",sut.getControlCode());
    }
    @Test
    public void equalsSameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualToDifferentClass(){
        assertFalse(sut.equals("not an EscapeCode"));
    }
    @Test
    public void equalsSameValue(){
        TestUtil.assertEqualAndHashcodeSame(sut, new EscapeCode(code));
    }
    
    @Test
    public void notEqualDifferentCode(){
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, new EscapeCode((byte)(code+1)));
    }
}
