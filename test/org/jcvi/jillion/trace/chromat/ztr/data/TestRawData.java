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
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.RawData;
import org.junit.Test;
public class TestRawData {

    byte[] data = new byte[]{1,2,3,4,5,6,7};
    
    @Test
    public void parseReturnsSameDataAsInput() throws IOException{
        assertTrue(Arrays.equals(RawData.INSTANCE.parseData(data), data));
    }
    @Test
    public void encode() throws IOException{
    	byte[] actual = RawData.INSTANCE.encodeData(data);
    	assertEquals("size", actual.length, data.length+1);
    	assertEquals(actual[0], 0);
    	for(int i=1; i< actual.length; i++){
    		assertEquals(actual[i], data[i-1]);
    	}
    }
}
