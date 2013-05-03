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
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.junit.Before;
import org.junit.Test;
public class TestChunk {

    Chunk sut= Chunk.BASE;
    private InputStream mockInputStream;
    IOException expectedException = new IOException("expected");
    
    @Before
    public void setup(){        
        mockInputStream = createMock(InputStream.class);
    }
    
    @Test
    public void readLength() throws TraceDecoderException, IOException{
        int length = 20;
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(length);
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(4)))
                    .andAnswer(EasyMockUtil.writeArrayToInputStream(buf.array()));
        replay(mockInputStream);
        assertEquals(length, sut.readLength(mockInputStream));
        verify(mockInputStream);
    }
    
    @Test
    public void readLengthThrowsIOExceptionShouldWrapInTraceDecoderException() throws IOException{

        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(4)))
        .andThrow(expectedException);
        replay(mockInputStream);
        try{
            sut.readLength(mockInputStream);
            fail("should throw TraceDecoderException which WrapsIOException");
        }
        catch(TraceDecoderException e){
            assertEquals("error reading chunk length", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
        verify(mockInputStream);
    }
    @Test
    public void readLengthNotEnoughBytesReadShouldWrapInTraceDecoderException() throws IOException{
        byte[] tooSmall = new byte[]{1,2,3};
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(4)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(tooSmall));
        
        expect(mockInputStream.read(isA(byte[].class),eq(3),eq(1))).andReturn(-1);
        replay(mockInputStream);
        try{
            sut.readLength(mockInputStream);
            fail("should throw TraceDecoderException when too small");
        }
        catch(TraceDecoderException e){
            assertEquals("error reading chunk length", e.getMessage());
            assertTrue(e.getCause() instanceof EOFException);
          
        }
        verify(mockInputStream);
    }
    
    @Test
    public void readMetaData() throws IOException, TraceDecoderException{
        int lengthToSkip = 1234;
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(lengthToSkip);
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(4)))
                    .andAnswer(EasyMockUtil.writeArrayToInputStream(buf.array()));
        expect(mockInputStream.read()).andReturn(1);
        expect(mockInputStream.skip(lengthToSkip-1)).andReturn((long)lengthToSkip-1);
        replay(mockInputStream);
        sut.readMetaData(mockInputStream);
        verify(mockInputStream);
    }
    @Test
    public void readMetaDataSkipThrowsException() throws IOException{
        int lengthToSkip = 1234;
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(lengthToSkip);
        expect(mockInputStream.read(isA(byte[].class),eq(0),eq(4)))
                    .andAnswer(EasyMockUtil.writeArrayToInputStream(buf.array()));
        expect(mockInputStream.read()).andThrow(expectedException);
        replay(mockInputStream);
        try{
            sut.readMetaData(mockInputStream);
            fail("should wrap IOException in TraceDecoderException");
        }catch(TraceDecoderException e){
            assertEquals("error reading chunk meta data", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
        verify(mockInputStream);
    }
    
    
}
