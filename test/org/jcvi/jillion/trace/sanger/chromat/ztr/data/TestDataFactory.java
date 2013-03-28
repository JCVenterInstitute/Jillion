/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat.ztr.data;

import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.DataFactory;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.DeltaEncodedData;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.FollowData;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.RawData;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.RunLengthEncodedData;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.ShrinkToEightBitData;
import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.ZLibData;
import org.jcvi.jillion.trace.TraceDecoderException;
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
        assertSame(DataFactory.getDataImplementation(new byte[]{0} ),
                RawData.INSTANCE);
    }
    @Test
    public void oneIsRunLengthEncoded() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{1} ),
                RunLengthEncodedData.INSTANCE);
    }

    @Test
    public void twoIsZLibData() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{2} ),
                ZLibData.INSTANCE);
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
        assertSame(DataFactory.getDataImplementation(new byte[]{70} ),
                ShrinkToEightBitData.SHORT_TO_BYTE);
    }
    @Test
    public void seventyoneIs32_to_8() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{71} ),
                ShrinkToEightBitData.INTEGER_TO_BYTE);
    }
    @Test
    public void seventyoneIsFollow() throws TraceDecoderException{
        assertSame(DataFactory.getDataImplementation(new byte[]{72} ),
                FollowData.INSTANCE);
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
