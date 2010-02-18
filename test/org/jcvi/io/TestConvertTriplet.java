/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestConvertTriplet {

    @Test
    public void convert(){
        //010011010110000101101110
        int triplet = 5071214;
        byte[] actual =Base64.convertTriplet(triplet);
        byte[] expected = new byte[]{19,22,5,46};
        assertArrayEquals(expected, actual);
    }
}
