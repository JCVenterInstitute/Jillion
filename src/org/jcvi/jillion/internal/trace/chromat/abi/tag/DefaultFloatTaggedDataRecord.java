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
package org.jcvi.jillion.internal.trace.chromat.abi.tag;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DefaultFloatTaggedDataRecord extends AbstractTaggedDataRecord<FloatArrayTaggedDataRecord,float[]> implements FloatArrayTaggedDataRecord{

	public DefaultFloatTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected float[] parseDataFrom(byte[] data) {
		//have to manually build
		ByteBuffer buffer= ByteBuffer.wrap(data);
		FloatBuffer result = FloatBuffer.allocate(data.length/4);
		while(buffer.hasRemaining()){
			result.put(buffer.getFloat());
		}
		return result.array();
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<float[]> getParsedDataType() {
        return float[].class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<FloatArrayTaggedDataRecord> getType() {
        return FloatArrayTaggedDataRecord.class;
    }

}
