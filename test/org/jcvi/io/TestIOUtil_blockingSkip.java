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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestIOUtil_blockingSkip {
    InputStream mockStream;
    @Before
    public void setup(){
        mockStream = createMock(InputStream.class);
    }
    @Test
    public void skipZeroBytesShouldDoNothing() throws IOException{
        replay(mockStream);
        IOUtil.blockingSkip(mockStream, 0);
        verify(mockStream);
    }
    @Test
    public void fullSkip() throws IOException{
        long bytesToSkip = 12345L;
        expect(mockStream.skip(bytesToSkip)).andReturn(bytesToSkip);
        replay(mockStream);
        IOUtil.blockingSkip(mockStream, bytesToSkip);
        verify(mockStream);
    }

    @Test
    public void throwsIOException() throws IOException{
        long bytesToSkip = 12345L;
        IOException expected = new IOException("expected");
        expect(mockStream.skip(bytesToSkip)).andThrow(expected);
        replay(mockStream);
        try {
            IOUtil.blockingSkip(mockStream, bytesToSkip);
            fail("should rethrow ioException");
        } catch (IOException e) {
            assertEquals(expected, e);
        }
        verify(mockStream);
    }

    @Test
    public void block() throws IOException{
        long totalbytesToSkip = 12345L;
        long half = totalbytesToSkip/2;
        long rest = totalbytesToSkip-half;
        expect(mockStream.skip(totalbytesToSkip)).andReturn(half);
        expect(mockStream.skip(rest)).andReturn(rest);
        replay(mockStream);
        IOUtil.blockingSkip(mockStream, totalbytesToSkip);
        verify(mockStream);
    }

}
