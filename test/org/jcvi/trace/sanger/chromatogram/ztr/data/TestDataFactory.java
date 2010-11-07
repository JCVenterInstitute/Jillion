/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.data.DataFactory;
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
    public void zeroIsRawData() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{0} ) instanceof RawData);
    }
    @Test
    public void oneIsRunLengthEncoded() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{1} ) instanceof RunLengthEncodedData);
    }

    @Test
    public void twoIsZLibData() throws TraceDecoderException{
        assertTrue(DataFactory.getDataImplementation(new byte[]{2} ) instanceof ZLibData);
    }
    @Test
    public void siztyFourIs8bit() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{64} ),
        		DeltaEncodedData.BYTE);
    }
    @Test
    public void siztyFiveIs16bit() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{65} ),
        		DeltaEncodedData.SHORT);
    }
    @Test
    public void siztySixIs32bit() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{66} ),
        		DeltaEncodedData.INTEGER);
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
