package org.jcvi.trace.sanger.chromatogram.ab1.tag;

import java.nio.ByteBuffer;

public class FloatTaggedDataRecord extends AbstractTaggedDataRecord<float[]>{

	public FloatTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected float[] parseDataFrom(byte[] data) {
		return ByteBuffer.wrap(data).asFloatBuffer().array();

	}

}
