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
 * Created on Feb 25, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_convertSignedToUnsigned {

    @Test
    public void convertByte(){
        assertEquals(0, IOUtil.toUnsignedByte((byte)0));
        assertEquals(50, IOUtil.toUnsignedByte((byte)50));
        assertEquals(135, IOUtil.toUnsignedByte((byte)-121));
        assertEquals(Byte.MAX_VALUE, IOUtil.toUnsignedByte(Byte.MAX_VALUE));
        assertEquals(Byte.MAX_VALUE+1, IOUtil.toUnsignedByte(Byte.MIN_VALUE));
        assertEquals(2*Byte.MAX_VALUE+1, IOUtil.toUnsignedByte((byte)-1));       
        assertEquals(Byte.MAX_VALUE, IOUtil.toUnsignedByte((byte)(Byte.MIN_VALUE -1)));
    }
    
    

	@Test
    public void convertShort(){
        assertEquals(0, IOUtil.toUnsignedShort((short)0));
        assertEquals(256, IOUtil.toUnsignedShort((short)256));
        assertEquals(50000, IOUtil.toUnsignedShort((short)50000));
        assertEquals(Short.MAX_VALUE, IOUtil.toUnsignedShort(Short.MAX_VALUE));
        assertEquals(Short.MAX_VALUE+1, IOUtil.toUnsignedShort(Short.MIN_VALUE));
        assertEquals(2*Short.MAX_VALUE+1, IOUtil.toUnsignedShort((short)-1));
        assertEquals(Short.MAX_VALUE, IOUtil.toUnsignedShort((short)(Short.MIN_VALUE -1)));
    }
    @Test
    public void convertInt(){
        assertEquals(0, IOUtil.toUnsignedInt(0));
        assertEquals(Short.MAX_VALUE+1, IOUtil.toUnsignedInt(Short.MAX_VALUE+1));
        assertEquals(100*Short.MAX_VALUE+1, IOUtil.toUnsignedInt(100*Short.MAX_VALUE+1));
        assertEquals(Integer.MAX_VALUE, IOUtil.toUnsignedInt(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE+1L, IOUtil.toUnsignedInt(Integer.MIN_VALUE));
        assertEquals(2L*Integer.MAX_VALUE+1, IOUtil.toUnsignedInt(-1));
        assertEquals(Integer.MAX_VALUE, IOUtil.toUnsignedInt(Integer.MIN_VALUE -1));
    }
}
