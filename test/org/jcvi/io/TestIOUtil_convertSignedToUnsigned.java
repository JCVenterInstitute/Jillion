/*
 * Created on Feb 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_convertSignedToUnsigned {

    @Test
    public void convertByte(){
        assertEquals(0, IOUtil.convertToUnsignedByte((byte)0));
        assertEquals(50, IOUtil.convertToUnsignedByte((byte)50));
        assertEquals(135, IOUtil.convertToUnsignedByte((byte)-121));
        assertEquals(Byte.MAX_VALUE, IOUtil.convertToUnsignedByte(Byte.MAX_VALUE));
        assertEquals(Byte.MAX_VALUE+1, IOUtil.convertToUnsignedByte(Byte.MIN_VALUE));
        assertEquals(2*Byte.MAX_VALUE+1, IOUtil.convertToUnsignedByte((byte)-1));       
        assertEquals(Byte.MAX_VALUE, IOUtil.convertToUnsignedByte((byte)(Byte.MIN_VALUE -1)));
    }
    
    @Test
    public void convertShort(){
        assertEquals(0, IOUtil.convertToUnsignedShort((short)0));
        assertEquals(256, IOUtil.convertToUnsignedShort((short)256));
        assertEquals(50000, IOUtil.convertToUnsignedShort((short)50000));
        assertEquals(Short.MAX_VALUE, IOUtil.convertToUnsignedShort(Short.MAX_VALUE));
        assertEquals(Short.MAX_VALUE+1, IOUtil.convertToUnsignedShort(Short.MIN_VALUE));
        assertEquals(2*Short.MAX_VALUE+1, IOUtil.convertToUnsignedShort((short)-1));
        assertEquals(Short.MAX_VALUE, IOUtil.convertToUnsignedShort((short)(Short.MIN_VALUE -1)));
    }
    @Test
    public void convertInt(){
        assertEquals(0, IOUtil.convertToUnsignedInt(0));
        assertEquals(Short.MAX_VALUE+1, IOUtil.convertToUnsignedInt(Short.MAX_VALUE+1));
        assertEquals(100*Short.MAX_VALUE+1, IOUtil.convertToUnsignedInt(100*Short.MAX_VALUE+1));
        assertEquals(Integer.MAX_VALUE, IOUtil.convertToUnsignedInt(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE+1L, IOUtil.convertToUnsignedInt(Integer.MIN_VALUE));
        assertEquals(2L*Integer.MAX_VALUE+1, IOUtil.convertToUnsignedInt(-1));
        assertEquals(Integer.MAX_VALUE, IOUtil.convertToUnsignedInt(Integer.MIN_VALUE -1));
    }
}
