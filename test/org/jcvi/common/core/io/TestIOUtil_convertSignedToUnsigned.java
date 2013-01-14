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
 * Created on Feb 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.io;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_convertSignedToUnsigned {

    @Test
    public void convertByte(){
        assertEquals(0, IOUtil.toUnsignedByte((byte)0));
        assertEquals(50, IOUtil.toUnsignedByte((byte)50));
        assertEquals(135, IOUtil.toUnsignedByte((byte)-121));
        assertEquals(Byte.MAX_VALUE, IOUtil.toUnsignedByte(Byte.MAX_VALUE));
        assertEquals(Byte.MAX_VALUE+1, IOUtil.toUnsignedByte(Byte.MIN_VALUE));
        assertEquals(2*Byte.MAX_VALUE+1, IOUtil.toUnsignedByte((byte)-1));       
        assertEquals(Byte.MAX_VALUE, IOUtil.toUnsignedByte((byte)(Byte.MIN_VALUE -1)));
    }
    
    

	@Test
    public void convertShort(){
        assertEquals(0, IOUtil.toUnsignedShort((short)0));
        assertEquals(256, IOUtil.toUnsignedShort((short)256));
        assertEquals(50000, IOUtil.toUnsignedShort((short)50000));
        assertEquals(Short.MAX_VALUE, IOUtil.toUnsignedShort(Short.MAX_VALUE));
        assertEquals(Short.MAX_VALUE+1, IOUtil.toUnsignedShort(Short.MIN_VALUE));
        assertEquals(2*Short.MAX_VALUE+1, IOUtil.toUnsignedShort((short)-1));
        assertEquals(Short.MAX_VALUE, IOUtil.toUnsignedShort((short)(Short.MIN_VALUE -1)));
    }
    @Test
    public void convertInt(){
        assertEquals(0, IOUtil.toUnsignedInt(0));
        assertEquals(Short.MAX_VALUE+1, IOUtil.toUnsignedInt(Short.MAX_VALUE+1));
        assertEquals(100*Short.MAX_VALUE+1, IOUtil.toUnsignedInt(100*Short.MAX_VALUE+1));
        assertEquals(Integer.MAX_VALUE, IOUtil.toUnsignedInt(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE+1L, IOUtil.toUnsignedInt(Integer.MIN_VALUE));
        assertEquals(2L*Integer.MAX_VALUE+1, IOUtil.toUnsignedInt(-1));
        assertEquals(Integer.MAX_VALUE, IOUtil.toUnsignedInt(Integer.MIN_VALUE -1));
    }
}
