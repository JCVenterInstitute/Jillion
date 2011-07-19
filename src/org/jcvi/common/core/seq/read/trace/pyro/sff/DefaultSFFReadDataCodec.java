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
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.DataInputStream;
import java.io.IOException;

import org.jcvi.common.core.io.IOUtil;

public class DefaultSFFReadDataCodec implements SFFReadDataCodec {

    @Override
    public SFFReadData decode(DataInputStream in, int numberOfFlows, int numberOfBases) throws SFFDecoderException {
        try{
            short[] values = IOUtil.readShortArray(in, numberOfFlows);
            byte[] indexes = IOUtil.readByteArray(in, numberOfBases);
            String bases = new String(IOUtil.readByteArray(in, numberOfBases));
            byte[] qualities = IOUtil.readByteArray(in, numberOfBases);

            int readDataLength = SFFUtil.getReadDataLength(numberOfFlows, numberOfBases);
            int padding =SFFUtil.caclulatePaddedBytes(readDataLength);
            IOUtil.blockingSkip(in, padding);
            return new DefaultSFFReadData(bases, indexes, values,qualities);
        }
        catch(IOException e){
            throw new SFFDecoderException("error decoding read data", e);
        }

    }

}
