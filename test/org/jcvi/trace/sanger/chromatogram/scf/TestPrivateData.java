/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import java.nio.ByteBuffer;

import org.jcvi.TestUtil;
import org.jcvi.trace.sanger.chromatogram.scf.PrivateData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestPrivateData {
    private static final byte[] DIFFERENT_DATA = new byte[]{1,2,3,4,5};
    private static final byte[] DATA = new byte[]{20,30,40, -20, -67,125};
    private PrivateData sut = new PrivateData(DATA);

    @Test
    public void constructor(){
        assertArrayEquals(DATA, sut.getData().array());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        PrivateData sameValues = new PrivateData(DATA);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void equalsSameArrayDifferentPosition(){
        PrivateData sameValues = new PrivateData(DATA);
        sameValues.getData().position(4);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not PrivateData"));
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsPrivateDataIsNull(){
        PrivateData nullData = new PrivateData((ByteBuffer)null);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    @Test
    public void notEqualsPrivateDataIsEmpty(){
        PrivateData nullData = new PrivateData(new byte[0]);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    @Test
    public void notEqualsPrivateDataIsDifferent(){
        PrivateData nullData = new PrivateData(DIFFERENT_DATA);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, nullData);
    }
    
    @Test
    public void bothPrivateDataIsNullShouldBeEquals(){
        PrivateData nullData1 = new PrivateData((ByteBuffer)null);
        PrivateData nullData2 = new PrivateData((ByteBuffer)null);
        TestUtil.assertEqualAndHashcodeSame(nullData1, nullData2);
    }
}
