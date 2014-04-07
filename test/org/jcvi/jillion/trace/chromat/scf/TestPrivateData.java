/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.PrivateDataImpl;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPrivateData {
    private static final byte[] DIFFERENT_DATA = new byte[]{1,2,3,4,5};
    private static final byte[] DATA = new byte[]{20,30,40, -20, -67,125};
    private PrivateData sut = new PrivateDataImpl(DATA);

    @Test
    public void constructor(){
        assertArrayEquals(DATA, sut.getBytes());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        PrivateData sameValues = new PrivateDataImpl(DATA);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not PrivateData"));
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    
    @Test
    public void notEqualsPrivateDataIsEmpty(){
        PrivateData nullData = new PrivateDataImpl(new byte[0]);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    @Test
    public void notEqualsPrivateDataIsDifferent(){
        PrivateData nullData = new PrivateDataImpl(DIFFERENT_DATA);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    
    
}
