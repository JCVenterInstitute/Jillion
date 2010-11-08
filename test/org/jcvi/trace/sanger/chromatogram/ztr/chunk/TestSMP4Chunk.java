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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSMP4Chunk {

    private static short[] aTraces = new short[]{0,0,2,4,5,3,2,0,0,0,1};
    private static short[] cTraces = new short[]{7,5,2,0,1,0,2,1,1,0,1};
    private static short[] gTraces = new short[]{1,0,0,2,1,0,3,8,4,2,0};
    private static short[] tTraces = new short[]{0,0,2,4,2,3,2,0,5,8,25};
   
    Chunk sut = Chunk.SMP4;

    
    @Test
    public void valid() throws TraceDecoderException{
        ByteBuffer buf = ByteBuffer.allocate(aTraces.length *8 + 2);
        buf.putShort((short)0);
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(aTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(cTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(gTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(tTraces[i]);
        }
        ZTRChromatogramBuilder struct = new ZTRChromatogramBuilder();

        sut.parseData(buf.array(), struct);
        assertTrue(Arrays.equals(struct.aPositions(),aTraces));
        assertTrue(Arrays.equals(struct.cPositions(),cTraces));
        assertTrue(Arrays.equals(struct.gPositions(),gTraces));
        assertTrue(Arrays.equals(struct.tPositions(),tTraces));
    }
    
}
