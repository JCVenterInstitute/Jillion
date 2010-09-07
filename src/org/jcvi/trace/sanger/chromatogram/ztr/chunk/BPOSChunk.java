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
 * Created on Nov 3, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;


/**
 * The <code>BPOSChunk</code> Chunk contains the positions of the
 * bases (peaks)stored as ints.
 * @author dkatzel
 *
 *
 */
public class BPOSChunk extends Chunk {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
            throws org.jcvi.trace.TraceDecoderException {
        final int numberOfBases = (unEncodedData.length -1)/4;
        ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
        ByteBuffer input = ByteBuffer.wrap(unEncodedData);
        //skip padding
        input.position(4);
        while(input.hasRemaining()){
            peaks.put((short) input.getInt());
        }
        builder.peaks(peaks.array());

    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected String parseData(byte[] unEncodedData,
            ChromatogramFileVisitor visitor,String basecalls) throws TraceDecoderException {
        final int numberOfBases = (unEncodedData.length -1)/4;
        ShortBuffer peaks = ShortBuffer.allocate(numberOfBases);
        ByteBuffer input = ByteBuffer.wrap(unEncodedData);
        //skip padding
        input.position(4);
        while(input.hasRemaining()){
            peaks.put((short) input.getInt());
        }
        visitor.visitPeaks(peaks.array());
        
        return basecalls;
        
    }
    
    

}
