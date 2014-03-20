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
import java.nio.ShortBuffer;

public class DefaultShortArrayTaggedDataRecord extends AbstractTaggedDataRecord<ShortArrayTaggedDataRecord,short[]> implements ShortArrayTaggedDataRecord{

	public DefaultShortArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected short[] parseDataFrom(byte[] data) {
		//have to manually build short array 
		ByteBuffer buffer= ByteBuffer.wrap(data);
		ShortBuffer result = ShortBuffer.allocate(data.length/2);
		while(buffer.hasRemaining()){
			result.put(buffer.getShort());
		}
		return result.array();
		
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<short[]> getParsedDataType() {
        return short[].class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<ShortArrayTaggedDataRecord> getType() {
        return ShortArrayTaggedDataRecord.class;
    }

}
