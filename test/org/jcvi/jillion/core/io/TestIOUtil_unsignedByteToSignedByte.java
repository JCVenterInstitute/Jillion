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
package org.jcvi.jillion.core.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestIOUtil_unsignedByteToSignedByte {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, (byte)0});
        data.add(new Object[]{50, (byte)50});
        data.add(new Object[]{100, (byte)100});
        data.add(new Object[]{255, (byte)-1});
        data.add(new Object[]{252, (byte)-4});
        data.add(new Object[]{156, (byte)-100});
        data.add(new Object[]{Byte.MAX_VALUE, Byte.MAX_VALUE});
        data.add(new Object[]{128, Byte.MIN_VALUE});
        return data;
    }
    
    private final int unsigned;
    private final byte signed;
    /**
     * @param unsigned
     * @param signed
     */
    public TestIOUtil_unsignedByteToSignedByte(int unsigned, byte signed) {
        this.unsigned = unsigned;
        this.signed = signed;
    }
    
    @Test
    public void convertUnsignedToSigned(){
        assertEquals(IOUtil.toSignedByte(unsigned), signed);
    }
    
}
