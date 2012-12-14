/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.common.core.seq.trace.sanger.chromat.ab1.tag;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DefaultIntegerArrayTaggedDataRecord  extends AbstractTaggedDataRecord<IntArrayTaggedDataRecord,int[]> implements IntArrayTaggedDataRecord{

	public DefaultIntegerArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected int[] parseDataFrom(byte[] data) {		
		//have to manually build
		ByteBuffer buffer= ByteBuffer.wrap(data);
		IntBuffer result = IntBuffer.allocate(data.length/4);
		while(buffer.hasRemaining()){
			result.put(buffer.getInt());
		}
		return result.array();
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<int[]> getParsedDataType() {
        return int[].class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<IntArrayTaggedDataRecord> getType() {
        return IntArrayTaggedDataRecord.class;
    }


}
