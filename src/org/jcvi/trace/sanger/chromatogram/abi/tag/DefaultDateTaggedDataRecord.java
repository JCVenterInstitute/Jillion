package org.jcvi.trace.sanger.chromatogram.abi.tag;

import java.nio.ByteBuffer;
import java.util.Date;

import org.joda.time.DateTime;

public class DefaultDateTaggedDataRecord extends AbstractTaggedDataRecord<Date>{

	public DefaultDateTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected Date parseDataFrom(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		short year =buf.getShort();
		byte month = buf.get();
		byte day = buf.get();
		
		return new DateTime(year, month, day, 0,0,0,0).toDate();
	}

}
