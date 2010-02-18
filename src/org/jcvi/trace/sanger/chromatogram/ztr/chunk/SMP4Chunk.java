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
 * Created on Oct 26, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;


import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;


/**
 * <code>SMP4Chunk</code> is the chromatogram scan points for all 4 channels
 * of trace samples.
 * The order of channels is A,C,G,T.  It is assumed that all channels are the
 * same length.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 *
 */
public class SMP4Chunk extends Chunk {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public void parseData(byte[] unEncodedData,ZTRChromatogramBuilder builder) throws TraceDecoderException {



        //read first 2 byte is padded bytes?
        
        ShortBuffer buf = ByteBuffer.wrap(unEncodedData).asShortBuffer();
        //skip padding
        buf.position(1);
        int length = buf.capacity()-1;
        int numberOfPositions = length/4;
        ShortBuffer aPositions = ShortBuffer.allocate(numberOfPositions);
        ShortBuffer cPositions = ShortBuffer.allocate(numberOfPositions);
        ShortBuffer gPositions = ShortBuffer.allocate(numberOfPositions);
        ShortBuffer tPositions = ShortBuffer.allocate(numberOfPositions);
        
        populatePositionData(buf, aPositions);
        populatePositionData(buf, cPositions);
        populatePositionData(buf, gPositions);
        populatePositionData(buf, tPositions);
        
          builder.aPositions(aPositions.array());
          builder.cPositions(cPositions.array());
          builder.gPositions(gPositions.array());
          builder.tPositions(tPositions.array());
          
    }


    private void populatePositionData(ShortBuffer buf, ShortBuffer aPositions) {
        for(int i=0; i< aPositions.capacity(); i++){
            aPositions.put(buf.get());
        }
    }




}
