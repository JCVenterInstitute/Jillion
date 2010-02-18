/*
 * Created on Dec 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.util.Arrays;

import org.jcvi.trace.sanger.chromatogram.ztr.data.SixteenBitToEightBitData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSixteenBitToEightBitData {
    static byte[] uncompressed = new byte[]{0,10,0,5,-1,-5,0,(byte)200,-4,-32};
    static byte[] compressed = new byte[]{70,10,5,-5,-128,0,(byte)200,-128,-4,-32};
    
    @Test
    public void decode(){
        SixteenBitToEightBitData sut = new SixteenBitToEightBitData();
        byte[] actual =sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
}
