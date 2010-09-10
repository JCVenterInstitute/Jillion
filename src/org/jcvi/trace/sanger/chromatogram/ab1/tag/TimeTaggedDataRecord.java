package org.jcvi.trace.sanger.chromatogram.ab1.tag;

import java.nio.ByteBuffer;

import org.joda.time.LocalTime;

public class TimeTaggedDataRecord extends AbstractTaggedDataRecord<LocalTime>{

	public TimeTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected LocalTime parseDataFrom(byte[] data) {
		ByteBuffer buf = ByteBuffer.wrap(data);
		
		return new LocalTime(buf.get(), buf.get(), buf.get());
	}

}
