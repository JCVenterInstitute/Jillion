/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestZTRUtil {

    @Test
    public void readInt(){
        ByteBuffer buf = ByteBuffer.allocate(4);
        int expected = 123456789;
        buf.putInt(expected);
        buf.flip();
        assertEquals(expected, ZTRUtil.readInt(buf.array()));
    }
    
    @Test
    public void switchEndian(){
        byte[] original = new byte[]{1,2,3,4,5,6,7,8};
        byte[] expected = new byte[]{8,7,6,5,4,3,2,1};
        
        assertTrue(Arrays.equals(expected, IOUtil.switchEndian(original)));
        assertTrue(Arrays.equals(original, IOUtil.switchEndian(expected)));
    }
    @Test
    public void switchEndianOddNumberOfElements(){
        byte[] original = new byte[]{1,2,3,4,5,6,7,8,9};
        byte[] expected = new byte[]{9,8,7,6,5,4,3,2,1};
        
        assertTrue(Arrays.equals(expected, IOUtil.switchEndian(original)));
        assertTrue(Arrays.equals(original, IOUtil.switchEndian(expected)));
    }
}
