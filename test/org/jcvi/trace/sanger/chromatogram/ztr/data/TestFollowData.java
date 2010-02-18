/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.data.FollowData;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFollowData {

    private static byte[] followBytes;
    //should have follow values [21, 1, -5, 4, -9, 17, 23]
    private byte[] uncompressedData = new byte[]{21,30,45,51,70,63,50};
    FollowData sut = new FollowData();
    @BeforeClass
    public static void setupFollowBytes(){
        followBytes = new byte[256];
        //for our test the follow byte is always +10
        for(int i=0; i<256; i++){
            followBytes[i] = (byte)((i+10)%256);
        }
    }
    
    @Test
    public void parse() throws TraceDecoderException{
        ByteBuffer compressed = ByteBuffer.allocate(1+256+uncompressedData.length);
        compressed.put((byte)72);// use follow format
        compressed.put(followBytes);
        compressed.put(uncompressedData[0]);
        byte prev = uncompressedData[0];
        for(int i=1; i<uncompressedData.length; i++){
            compressed.put((byte)(followBytes[prev] - uncompressedData[i] ));
            prev = uncompressedData[i];
        }

       byte[] actual = sut.parseData(compressed.array());
       assertTrue(Arrays.equals(actual, uncompressedData));
    }
}
