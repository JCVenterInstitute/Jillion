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


public class DefaultTimeTaggedDataRecord extends AbstractTaggedDataRecord<TimeTaggedDataRecord,Ab1LocalTime> implements TimeTaggedDataRecord{

	public DefaultTimeTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected Ab1LocalTime parseDataFrom(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		return new Ab1LocalTime(buf.get(), buf.get(), buf.get());
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<Ab1LocalTime> getParsedDataType() {
        return Ab1LocalTime.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<TimeTaggedDataRecord> getType() {
        return TimeTaggedDataRecord.class;
    }


}
