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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.DataFactory;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.DeltaEncodedData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.FollowData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.RawData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.RunLengthEncodedData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ShrinkToEightBitData;
import org.jcvi.jillion.internal.trace.chromat.ztr.data.ZLibData;
import org.junit.Test;
public class TestDataFactory {

    @Test
    public void nullDataArrayShouldThrowIOException(){
        try{
            DataFactory.getDataImplementation(null);
            fail("null array should throw IOException");
        }catch(IOException e){
            assertEquals("can not parse data format", e.getMessage());
        }
    }
    
    @Test
    public void emptyDataArrayShouldThrowIOException(){
        try{
            DataFactory.getDataImplementation(new byte[]{});
            fail("null array should throw IOException");
        }catch(IOException e){
            assertEquals("can not parse data format", e.getMessage());
        }
    }
    
    @Test
    public void zeroIsRawData() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{0} ),
                RawData.INSTANCE);
    }
    @Test
    public void oneIsRunLengthEncoded() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{1} ),
                RunLengthEncodedData.INSTANCE);
    }

    @Test
    public void twoIsZLibData() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{2} ),
                ZLibData.INSTANCE);
    }
    @Test
    public void siztyFourIs8bit() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{64} ),
        		DeltaEncodedData.BYTE);
    }
    @Test
    public void siztyFiveIs16bit() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{65} ),
        		DeltaEncodedData.SHORT);
    }
    @Test
    public void siztySixIs32bit() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{66} ),
        		DeltaEncodedData.INTEGER);
    }
    @Test
    public void seventyIs16_to_8() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{70} ),
                ShrinkToEightBitData.SHORT_TO_BYTE);
    }
    @Test
    public void seventyoneIs32_to_8() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{71} ),
                ShrinkToEightBitData.INTEGER_TO_BYTE);
    }
    @Test
    public void seventyoneIsFollow() throws IOException{
        assertSame(DataFactory.getDataImplementation(new byte[]{72} ),
                FollowData.INSTANCE);
    }
    @Test
    public void unknownthrowsIOException(){
        
        assertThrowsException((byte) -1);
        assertThrowsException((byte) 5);
        assertThrowsException((byte) 100);
    }

    private void assertThrowsException(byte unknownValue) {
        try {
            DataFactory.getDataImplementation(new byte[]{unknownValue} );
            fail("should throw IOException for " + unknownValue);
        } catch (IOException e) {
            assertEquals("format not supported : "+ unknownValue, e.getMessage() );
        }
    }
}
