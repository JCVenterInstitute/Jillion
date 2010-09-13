package org.jcvi.trace.sanger.chromatogram.abi.tag;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DefaultIntegerArrayTaggedDataRecord  extends AbstractTaggedDataRecord<int[]>{

	public DefaultIntegerArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected int[] parseDataFrom(byte[] data) {		
		//have to manually build
		ByteBuffer buffer= ByteBuffer.wrap(data);
		IntBuffer result = IntBuffer.allocate(data.length/4);
		while(buffer.hasRemaining()){
			result.put(buffer.getInt());
		}
		return result.array();
	}


}
