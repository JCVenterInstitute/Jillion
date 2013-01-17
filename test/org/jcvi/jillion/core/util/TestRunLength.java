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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.core.util.RunLength;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLength {

    String value = "string value";
    int length = 10;
    
    RunLength<String> sut = new RunLength<String>(value, length);
    @Test
    public void constructor(){
        assertEquals(value, sut.getValue());
        assertEquals(length, sut.getLength());
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a RunLength"));
    }
    
    @Test
    public void equalsSameValue(){
        RunLength<String> sameValues = new RunLength<String>(value, length);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentLengthShouldNotEqual(){
        RunLength<String> differentLength = new RunLength<String>(value, length+1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentLength);
    }
    @Test
    public void differentValueShouldNotEqual(){
        RunLength<String> differentValue = new RunLength<String>("different"+value, length);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValue);
    }
    @Test
    public void nullValueShouldNotEqual(){
        RunLength<String> nullValue = new RunLength<String>(null, length);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullValue);
    }
}
