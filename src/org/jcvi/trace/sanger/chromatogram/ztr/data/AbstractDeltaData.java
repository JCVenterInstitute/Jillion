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
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;

/**
 * <code>AbstractDeltaData</code> is an abstract
 * implementation of the Delta encoded Data formats.
 * The Delta formats store the differences between successive
 * bytes instead of the actual values.  Different implementations
 * of <code>AbstractDeltaData</code> are used for the different
 * sizes of the encoded values.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractDeltaData implements Data {
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data){
        //read level
        int level = data[1];
        int startPosition = 2 +getPaddingSize();
        ByteBuffer compressed = ByteBuffer.allocate(data.length-startPosition);
        compressed.put(data, startPosition, data.length-startPosition);
        compressed.flip();
        ByteBuffer unCompressedData = ByteBuffer.allocate(compressed.capacity());
        getDeltaStrategy(level).unCompress(compressed, unCompressedData);
        return unCompressedData.array();

    }
    /**
     * Retrieves which DeltaStrategy to use.
     * @param level
     * @return
     */
    protected abstract DeltaStrategy getDeltaStrategy(int level);
    /**
     * Some implementations may have additional
     * padding between the format byte and
     * when the actual data starts.  Usually this
     * is to needed to make the total length
     * of the data section divisible. Implementations
     * may override this method to return a different
     * padding size.
     * @return <code>0</code>
     */
    protected int getPaddingSize(){
        return 0;
    }
}
