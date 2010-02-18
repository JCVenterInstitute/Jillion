/*
 * Created on Dec 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.data.RunLengthEncodedData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncodedData {
    static byte[] uncompressedData = new byte[]{20,9,9,9,9,9,10,9,8,7};
    static byte[] encodedData = new byte[15];
    static byte guard = (byte)8;
    RunLengthEncodedData sut = new RunLengthEncodedData();
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
    public void parse() throws TraceDecoderException{

        //param isn't used?
        byte[] actualUncompressed = sut.parseData(encodedData);
        assertTrue(Arrays.equals(actualUncompressed, uncompressedData));
    }
    
    @Test
    public void parseLongHomopolomer() throws TraceDecoderException{
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
