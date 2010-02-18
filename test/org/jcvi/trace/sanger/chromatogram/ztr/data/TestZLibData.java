/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ZLibData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestZLibData {

    byte[] uncompressed = "blahblahblah??".getBytes();
    
    ZLibData sut = new ZLibData();
    @Test
    public void parse() throws TraceDecoderException{
        Deflater compresser = new Deflater();
        compresser.setInput(uncompressed);
        compresser.finish();
        byte[] compressed = new byte[100];
        int compressedDataLength = compresser.deflate(compressed);
        
        ByteBuffer compressedInput= ByteBuffer.allocate(5+compressedDataLength);
        compressedInput.put((byte)2); // use zlib
        compressedInput.put((byte)uncompressed.length);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        
        compressedInput.put(compressed, 0, compressedDataLength);
 
        byte[] actual = sut.parseData(compressedInput.array());
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void invalidParseShouldThrowTraceDeocoderException(){
        ByteBuffer compressedInput= ByteBuffer.allocate(5+uncompressed.length);
        compressedInput.put((byte)2); // use zlib
        compressedInput.put((byte)uncompressed.length);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        compressedInput.put((byte)0);
        
        compressedInput.put(uncompressed, 0, uncompressed.length);
        try {
            sut.parseData(compressedInput.array());
            fail("invalid zlib data should throw TraceDecoderException");
        } catch (TraceDecoderException e) {
            assertEquals("could not parse ZLibData", e.getMessage());
            assertTrue(e.getCause() instanceof DataFormatException);
        }
    }

}
