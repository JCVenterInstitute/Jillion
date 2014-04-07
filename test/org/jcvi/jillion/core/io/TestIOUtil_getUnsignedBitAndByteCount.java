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
package org.jcvi.jillion.core.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestIOUtil_getUnsignedBitAndByteCount {

    private final long value, numBits, numBytes;

    @Parameters
    public static Collection<?> data(){
        List<Long[]> data = new ArrayList<Long[]>();
  
        data.add(new Long[]{1L,1L,1L});
        data.add(new Long[]{2L,2L,1L});
        data.add(new Long[]{3L,2L,1L});
        data.add(new Long[]{4L,3L,1L});
        data.add(new Long[]{5L,3L,1L});
        data.add(new Long[]{6L,3L,1L});
        data.add(new Long[]{7L,3L,1L});
        
        for(long i=8; i<16; i++){
            data.add(new Long[]{i,4L,1L});
        }
        for(long i=16; i<32; i++){
            data.add(new Long[]{i,5L,1L});
        }
        for(long i=32; i<64; i++){
            data.add(new Long[]{i,6L,1L});
        }
        for(long i=64; i<128; i++){
            data.add(new Long[]{i,7L,1L});
        }
        for(long i=128; i<256; i++){
            data.add(new Long[]{i,8L,1L});
        }
        for(long i=256; i<512; i++){
            data.add(new Long[]{i,9L,2L});
        }
        //spot checks
        data.add(new Long[]{(long)Short.MAX_VALUE,15L,2L});
        data.add(new Long[]{2L* Short.MAX_VALUE-1,16L,2L});
        
        data.add(new Long[]{(long)Integer.MAX_VALUE,32L,4L});
        data.add(new Long[]{2L*Integer.MAX_VALUE -1,32L,4L});
        
        data.add(new Long[]{Long.MAX_VALUE,64L,8L});
        
        return data;
    }
    
    public TestIOUtil_getUnsignedBitAndByteCount(long value, long numBits,
            long numBytes) {
        this.value = value;
        this.numBits = numBits;
        this.numBytes = numBytes;
    }
    
    @Test
    public void numBits(){
        assertEquals(numBits, IOUtil.getUnsignedBitCount(value));
    }
    @Test
    public void numBytes(){
        assertEquals(numBytes, IOUtil.getUnsignedByteCount(value));
    }
    
}
