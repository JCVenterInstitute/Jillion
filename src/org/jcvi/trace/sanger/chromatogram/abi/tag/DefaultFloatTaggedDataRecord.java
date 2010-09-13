package org.jcvi.trace.sanger.chromatogram.abi.tag;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DefaultFloatTaggedDataRecord extends AbstractTaggedDataRecord<float[]>{

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

}
