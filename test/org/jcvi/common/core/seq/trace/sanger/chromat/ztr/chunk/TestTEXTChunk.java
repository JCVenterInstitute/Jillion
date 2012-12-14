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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.ztr.chunk;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.TraceEncoderException;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRChromatogram;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.ZTRChromatogramBuilder;
import org.jcvi.common.core.seq.trace.sanger.chromat.ztr.chunk.Chunk;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;
public class TestTEXTChunk {

    private static final byte NULL_TERMINATOR = 0;
    static Map<String,String> expected ;
    Chunk sut = Chunk.COMMENTS;
    
    private static final byte[] encodedBytes;
    static{
    	 expected = new LinkedHashMap<String,String>();
         expected.put("DATE", "Sun 09 Sep 20:29:52 2007 to Sun 09 Sep 21:48:16 2007");
         expected.put("SIGN", "A:5503,C:5140,G:3030,T:5266");
         expected.put("NAME", "TIGR_GBKAK82TF_980085_1106817232495");
         
    	ByteBuffer buf = ByteBuffer.allocate(134);
        buf.put((byte)0);
        for(Entry<String, String>  entry : expected.entrySet()){
            final String key = entry.getKey().toString();
            final String value = entry.getValue().toString();
            buf.put(key.getBytes());
            buf.put(NULL_TERMINATOR);
            buf.put(value.getBytes());
            buf.put(NULL_TERMINATOR);
        }
        buf.put(NULL_TERMINATOR);
        
        encodedBytes = buf.array();
    }
   
    
    @Test
    public void parse() throws TraceDecoderException{
        
        ZTRChromatogramBuilder struct = new ZTRChromatogramBuilder("id");
        sut.parseData(encodedBytes, struct);
        assertEquals(struct.properties(), expected);
    }

    	@Test
        public void encode() throws TraceEncoderException, TraceDecoderException{
        	ZTRChromatogram mockChromatogram = createMock(ZTRChromatogram.class);
        	expect(mockChromatogram.getComments()).andReturn(expected);
        	
        	replay(mockChromatogram);
        	byte[] actual =sut.encodeChunk(mockChromatogram);
        	assertArrayEquals(encodedBytes, actual);
        	verify(mockChromatogram);
    }
    
    
}
