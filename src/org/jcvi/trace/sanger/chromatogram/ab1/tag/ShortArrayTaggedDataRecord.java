package org.jcvi.trace.sanger.chromatogram.ab1.tag;

import java.nio.ByteBuffer;

public class ShortArrayTaggedDataRecord extends AbstractTaggedDataRecord<short[]>{

	public ShortArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected short[] parseDataFrom(byte[] data) {
		return ByteBuffer.wrap(data).asShortBuffer().array();
		
	}

}
