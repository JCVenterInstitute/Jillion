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

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ChromatogramFileVisitor;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;



/**
 * The <code>BASEChunk</code> contains the actual base calls
 * for this Chromatogram.
 * @author dkatzel
 *
 *
 */
public class BASEChunk extends Chunk {

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    protected void parseData(byte[] unEncodedData, ZTRChromatogramBuilder builder)
            throws TraceDecoderException {
        //first byte is padding
        final int numberOfBases = unEncodedData.length -1;
        ByteBuffer buf = ByteBuffer.allocate(numberOfBases);
        buf.put(unEncodedData, 1, numberOfBases);
        builder.basecalls(new String(buf.array()));

    }

    @Override
    protected String parseData(byte[] unEncodedData,
            ChromatogramFileVisitor visitor,String ignored) throws TraceDecoderException {
      //first byte is padding
        final int numberOfBases = unEncodedData.length -1;
        ByteBuffer buf = ByteBuffer.allocate(numberOfBases);
        buf.put(unEncodedData, 1, numberOfBases);
        
        final String basecalls = new String(buf.array());
        visitor.visitBasecalls(basecalls);
        return basecalls;
        
    }
    
    

}
