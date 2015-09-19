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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;

public class TestIOUtil_readShortArray {

    short[] shortArray = new short[]{130, 20,260, 6500};
    byte[] expectedByteArray;
    @org.junit.Before
    public void setup(){
        ByteBuffer buf = ByteBuffer.allocate(shortArray.length *2);
        for(int i=0; i< shortArray.length; i++){
            buf.putShort(shortArray[i]);
        }
        expectedByteArray = buf.array();
    }
    @Test
    public void valid() throws IOException{
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(expectedByteArray));
        short[] actualArray = IOUtil.readShortArray(in, shortArray.length);
        assertTrue(Arrays.equals(shortArray, actualArray));
    }

    @Test
    public void didNotReadEnough(){
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(expectedByteArray));
        try {
            IOUtil.readShortArray(in, shortArray.length+1);
            fail("if did not read exected length should throw IOException");
        } catch (IOException expected) {

        }

    }
}
