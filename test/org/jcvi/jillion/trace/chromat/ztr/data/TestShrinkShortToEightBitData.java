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
 * Created on Dec 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import java.util.Arrays;

import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ShrinkToEightBitData;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestShrinkShortToEightBitData {
    static byte[] uncompressed = new byte[]{0,10,0,5,-1,-5,0,(byte)200,-4,-32,3,32};
    static byte[] compressed = new byte[]{70,10,5,-5,-128,0,(byte)200,-128,-4,-32,-128,3,32};
    
    @Test
    public void decode() throws TraceDecoderException{
        Data sut = ShrinkToEightBitData.SHORT_TO_BYTE;
        byte[] actual =sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void encode() throws TraceEncoderException{
    	Data sut = ShrinkToEightBitData.SHORT_TO_BYTE;
    	byte[] actual = sut.encodeData(uncompressed);
    	assertTrue(Arrays.equals(actual, compressed));
    }
}
