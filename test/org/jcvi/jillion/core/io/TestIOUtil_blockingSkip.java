/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
public class TestIOUtil_blockingSkip {
    InputStream mockStream;
    private static final int NOT_EOF = 1;
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
        expect(mockStream.read()).andReturn(NOT_EOF);
        expect(mockStream.skip(bytesToSkip-1)).andReturn(bytesToSkip-1);
        replay(mockStream);
        IOUtil.blockingSkip(mockStream, bytesToSkip);
        verify(mockStream);
    }

    @Test
    public void throwsIOException() throws IOException{
        long bytesToSkip = 12345L;
        IOException expected = new IOException("expected");
        expect(mockStream.read()).andReturn(NOT_EOF);
        expect(mockStream.skip(bytesToSkip-1)).andThrow(expected);
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
        long half = (totalbytesToSkip-1)/2;
        long rest = totalbytesToSkip-2-half;
        expect(mockStream.read()).andReturn(NOT_EOF);
        expect(mockStream.skip(totalbytesToSkip-1)).andReturn(half);
        expect(mockStream.read()).andReturn(NOT_EOF);
        expect(mockStream.skip(rest)).andReturn(rest);
        replay(mockStream);
        IOUtil.blockingSkip(mockStream, totalbytesToSkip);
        verify(mockStream);
    }

    @Test
    public void skipPastEOFShouldThrowIOException(){
    	byte[] buf = new byte[10];
    	ByteArrayInputStream in = new ByteArrayInputStream(buf);
    	try {
			IOUtil.blockingSkip(in, buf.length+5);
			fail("should throw IOException if skip past end of file");
		} catch (IOException e) {
			//expected
			assertTrue(e.getMessage().contains("end of file"));
		}
    }
}
