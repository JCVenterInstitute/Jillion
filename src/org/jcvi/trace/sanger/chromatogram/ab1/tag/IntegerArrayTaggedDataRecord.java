package org.jcvi.trace.sanger.chromatogram.ab1.tag;

import java.nio.ByteBuffer;

public class IntegerArrayTaggedDataRecord  extends AbstractTaggedDataRecord<int[]>{

	public IntegerArrayTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected int[] parseDataFrom(byte[] data) {
		
		return ByteBuffer.wrap(data).asIntBuffer().array();
	}


}
