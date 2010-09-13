package org.jcvi.trace.sanger.chromatogram.abi.tag;

import java.nio.ByteBuffer;

import org.joda.time.LocalTime;

public class DefaultTimeTaggedDataRecord extends AbstractTaggedDataRecord<LocalTime>{

	public DefaultTimeTaggedDataRecord(TaggedDataName name, long number,
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
