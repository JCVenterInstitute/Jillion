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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestIOUtil_readByteArray {

    byte[] array = new byte[]{20,15,30,40};

    @Test
    public void valid() throws IOException{
        InputStream in = new ByteArrayInputStream(array);
        byte[] actualArray = IOUtil.toByteArray(in, array.length);
        assertTrue(Arrays.equals(array, actualArray));
    }
    @Test
    public void didNotReadEnough() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(array.length+1))).andReturn(array.length);
        expect(mockInputStream.read(isA(byte[].class), eq(array.length), eq(1))).andReturn(-1);
        replay(mockInputStream);
        try {
            IOUtil.toByteArray(mockInputStream, array.length+1);
            fail("if did not read exected length should throw IOException");
        } catch (IOException e) {
            String expectedMessage = "end of file after only "
                + array.length + " bytes read (expected "+ (array.length+1) +")";
            assertEquals(expectedMessage, e.getMessage());
        }

    }
}
