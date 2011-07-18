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
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data;

import java.util.Arrays;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.TraceEncoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data.RawData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRawData {

    byte[] data = new byte[]{1,2,3,4,5,6,7};
    
    @Test
    public void parseReturnsSameDataAsInput() throws TraceDecoderException{
        assertTrue(Arrays.equals(RawData.INSTANCE.parseData(data), data));
    }
    @Test
    public void encode() throws TraceEncoderException{
    	byte[] actual = RawData.INSTANCE.encodeData(data);
    	assertEquals("size", actual.length, data.length+1);
    	assertEquals(actual[0], 0);
    	for(int i=1; i< actual.length; i++){
    		assertEquals(actual[i], data[i-1]);
    	}
    }
}
