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
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Map.Entry;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.TEXTChunk;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestTEXTChunk {

    private static final byte NULL_TERMINATOR = 0;
    Properties expected ;
    TEXTChunk sut= new TEXTChunk();
    
    @Before
    public void setup(){
        expected = new Properties();
        expected.put("DATE", "Sun 09 Sep 20:29:52 2007 to Sun 09 Sep 21:48:16 2007");
        expected.put("SIGN", "A:5503,C:5140,G:3030,T:5266");
        expected.put("NAME", "TIGR_GBKAK82TF_980085_1106817232495");
       
    }
    
    @Test
    public void valid() throws TraceDecoderException{
        ByteBuffer buf = ByteBuffer.allocate(134);
        buf.put((byte)0);
        for(Entry<Object, Object>  entry : expected.entrySet()){
            final String key = entry.getKey().toString();
            final String value = entry.getValue().toString();
            buf.put(key.getBytes());
            buf.put(NULL_TERMINATOR);
            buf.put(value.getBytes());
            buf.put(NULL_TERMINATOR);
        }
        buf.put(NULL_TERMINATOR);
        ZTRChromatogramBuilder struct = new ZTRChromatogramBuilder();
        sut.parseData(buf.array(), struct);
        assertEquals(struct.properties(), expected);
    }
    
    @Test
    public void invalidShouldThrowTraceDecoderException() throws IOException{
        IOException expectedException = new IOException("expected");
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read()).andThrow(expectedException);
        replay(mockInputStream);
        try {
            sut.parseText(mockInputStream);
            fail("should throw TraceDecoderException on error");
        } catch (TraceDecoderException e) {
            assertEquals("error reading text data", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
        verify(mockInputStream);
    }
}
