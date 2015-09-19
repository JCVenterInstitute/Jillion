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
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestZTRUtil {

    @Test
    public void readInt(){
        ByteBuffer buf = ByteBuffer.allocate(4);
        int expected = 123456789;
        buf.putInt(expected);
        buf.flip();
        assertEquals(expected, ZTRUtil.readInt(buf.array()));
    }
    
    @Test
    public void switchEndian(){
        byte[] original = new byte[]{1,2,3,4,5,6,7,8};
        byte[] expected = new byte[]{8,7,6,5,4,3,2,1};
        
        assertTrue(Arrays.equals(expected, IOUtil.switchEndian(original)));
        assertTrue(Arrays.equals(original, IOUtil.switchEndian(expected)));
    }
    @Test
    public void switchEndianOddNumberOfElements(){
        byte[] original = new byte[]{1,2,3,4,5,6,7,8,9};
        byte[] expected = new byte[]{9,8,7,6,5,4,3,2,1};
        
        assertTrue(Arrays.equals(expected, IOUtil.switchEndian(original)));
        assertTrue(Arrays.equals(original, IOUtil.switchEndian(expected)));
    }
}
