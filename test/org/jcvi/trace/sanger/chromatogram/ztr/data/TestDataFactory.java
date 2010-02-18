/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DataFactory;
import org.jcvi.trace.sanger.chromatogram.ztr.data.Delta16Data;
import org.jcvi.trace.sanger.chromatogram.ztr.data.Delta32Data;
import org.jcvi.trace.sanger.chromatogram.ztr.data.Delta8Data;
import org.jcvi.trace.sanger.chromatogram.ztr.data.FollowData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.RawData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.RunLengthEncodedData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.SixteenBitToEightBitData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ThirtyTwoToEightBitData;
import org.jcvi.trace.sanger.chromatogram.ztr.data.ZLibData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDataFactory {

    @Test
    public void nullDataArrayShouldThrowTraceDecoderException(){
        try{
            DataFactory.getDataImplementation(null);
            fail("null array should throw TraceDecoderException");
        }catch(TraceDecoderException e){
            assertEquals("can not parse data format", e.getMessage());
        }
    }
    
    @Test
    public void emptyDataArrayShouldThrowTraceDecoderException(){
        try{
            DataFactory.getDataImplementation(new byte[]{});
            fail("null array should throw TraceDecoderException");
        }catch(TraceDecoderException e){
            assertEquals("can not parse data format", e.getMessage());
        }
    }
    
    @Test
    public void ZeroIsRawData() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{0} ) instanceof RawData);
    }
    @Test
    public void OneIsRunLengthEncoded() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{1} ) instanceof RunLengthEncodedData);
    }

    @Test
    public void TwoIsZLibData() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{2} ) instanceof ZLibData);
    }
    @Test
    public void siztyFourIs8bit() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{64} ) instanceof Delta8Data);
    }
    @Test
    public void siztyFiveIs16bit() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{65} ) instanceof Delta16Data);
    }
    @Test
    public void siztySixIs32bit() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{66} ) instanceof Delta32Data);
    }
    @Test
    public void seventyIs16_to_8() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{70} ) instanceof SixteenBitToEightBitData);
    }
    @Test
    public void seventyoneIs32_to_8() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{71} ) instanceof ThirtyTwoToEightBitData);
    }
    @Test
    public void seventyoneIsFollow() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{72} ) instanceof FollowData);
    }
    @Test
    public void unknownthrowsTraceDecoderException(){
        
        assertThrowsException((byte) -1);
        assertThrowsException((byte) 5);
        assertThrowsException((byte) 100);
    }

    private void assertThrowsException(byte unknownValue) {
        try {
            DataFactory.getDataImplementation(new byte[]{unknownValue} );
            fail("should throw TraceDecoderException for " + unknownValue);
        } catch (TraceDecoderException e) {
            assertEquals("format not supported : "+ unknownValue, e.getMessage() );
        }
    }
}
