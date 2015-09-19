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
 * Created on Dec 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ShrinkToEightBitData;
import org.junit.Test;
public class TestShrinkShortToEightBitData {
    static byte[] uncompressed = new byte[]{0,10,0,5,-1,-5,0,(byte)200,-4,-32,3,32};
    static byte[] compressed = new byte[]{70,10,5,-5,-128,0,(byte)200,-128,-4,-32,-128,3,32};
    
    @Test
    public void decode() throws IOException{
        Data sut = ShrinkToEightBitData.SHORT_TO_BYTE;
        byte[] actual =sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void encode() throws IOException{
    	Data sut = ShrinkToEightBitData.SHORT_TO_BYTE;
    	byte[] actual = sut.encodeData(uncompressed);
    	assertTrue(Arrays.equals(actual, compressed));
    }
}
