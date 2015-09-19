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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestIOUtil_unsignedShortToSignedShort {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, (short)0});
        data.add(new Object[]{50, (short)50});
        data.add(new Object[]{100, (short)100});
        data.add(new Object[]{Short.MAX_VALUE+1, Short.MIN_VALUE});
        data.add(new Object[]{Short.MAX_VALUE+4, (short)(Short.MIN_VALUE+3)});
        data.add(new Object[]{Short.MAX_VALUE+100, (short)(Short.MIN_VALUE+99)});
        data.add(new Object[]{Byte.MAX_VALUE, (short)Byte.MAX_VALUE});
        data.add(new Object[]{Short.MAX_VALUE+Byte.MIN_VALUE, (short)(Short.MIN_VALUE+Byte.MIN_VALUE-1)});
        
        return data;
    }
    
    private final int unsigned;
    private final short signed;
    /**
     * @param unsigned
     * @param signed
     */
    public TestIOUtil_unsignedShortToSignedShort(int unsigned, short signed) {
        this.unsigned = unsigned;
        this.signed = signed;
    }
    
    @Test
    public void convertUnsignedToSigned(){
        assertEquals(IOUtil.toSignedShort(unsigned), signed);
    }
}
