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
package org.jcvi.trace.sanger.chromatogram.abi.tag;

import java.nio.ByteBuffer;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class DefaultDateTaggedDataRecord extends AbstractTaggedDataRecord<DateTaggedDataRecord,LocalDate> implements DateTaggedDataRecord{

	public DefaultDateTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected LocalDate parseDataFrom(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		short year =buf.getShort();
		byte month = buf.get();
		byte day = buf.get();
		
		return new LocalDate(year, month, day);
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<LocalDate> getParsedDataType() {
        return LocalDate.class;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Class<DateTaggedDataRecord> getType() {
        return DateTaggedDataRecord.class;
    }



}
