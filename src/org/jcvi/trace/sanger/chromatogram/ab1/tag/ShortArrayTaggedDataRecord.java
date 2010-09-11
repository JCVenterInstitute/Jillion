package org.jcvi.trace.sanger.chromatogram.ab1.tag;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class ShortArrayTaggedDataRecord extends AbstractTaggedDataRecord<short[]>{

	public ShortArrayTaggedDataRecord(TaggedDataName name, long number,
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

}
