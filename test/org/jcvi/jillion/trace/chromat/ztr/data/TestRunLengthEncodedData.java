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
 * Created on Dec 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.RunLengthEncodedData;
import org.junit.Test;
public class TestRunLengthEncodedData {
    static byte[] uncompressedData = new byte[]{20,9,9,9,9,9,10,9,8,7};
    static byte[] encodedData = new byte[15];
    static byte guard = (byte)8;
    RunLengthEncodedData sut = RunLengthEncodedData.INSTANCE;
    static{
        
        ByteBuffer buf = ByteBuffer.wrap(encodedData);
        //header for runlength encoding
        buf.put((byte)1);
        //length is different endian?
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(uncompressedData.length);
        buf.put(IOUtil.switchEndian(lengthBuffer.array()));
        buf.put(guard);
        buf.put(uncompressedData[0]);
        buf.put(guard);
        buf.put((byte)5);
        buf.put(uncompressedData[0+1]);
        
        buf.put(uncompressedData[0+1+5]);
        buf.put(uncompressedData[0+1+5+1]);
        buf.put(guard);
        //a byte of 0 means value is same as guard but
        //is not meant to be a guard 
        buf.put((byte)0);
        buf.put(uncompressedData[0+1+5+1+1+1]);
        buf.flip();
    }
    
    @Test
    public void parse() throws IOException{
        byte[] actualUncompressed = sut.parseData(encodedData);
        assertTrue(Arrays.equals(actualUncompressed, uncompressedData));
    }
    @Test
    public void encode() throws IOException{
    	byte[] actual = sut.encodeData(uncompressedData, guard);
    	assertArrayEquals(actual, encodedData);
    }
    
    @Test
    public void parseLongHomopolomer() throws IOException{
        int repeatLength = 240;
        ByteBuffer buf = ByteBuffer.allocate(11);
        buf.put((byte)1);
        buf.put((byte)242);
        buf.put((byte)0);
        buf.put((byte)0);
        buf.put((byte)0);
        buf.put(guard);
        buf.put(uncompressedData[0]);
        buf.put(guard);
        buf.put((byte)repeatLength);
        buf.put(uncompressedData[0+1]);
        buf.put((byte)15);
        byte[] actual = sut.parseData(buf.array());
        byte[] expected = new byte[242];
        expected[0]=uncompressedData[0];
        for(int i=0; i<repeatLength; i++){
            expected[i+1] = uncompressedData[0+1];
        }
        expected[241] = 15;

        assertTrue(Arrays.equals(actual, expected));
        
        
    }
    
}
