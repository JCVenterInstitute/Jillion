package org.jcvi.trace.sanger.chromatogram.ab1.tag;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.sanger.chromatogram.ab1.Ab1Util;

public class PascalStringTaggedDataRecord extends AbstractTaggedDataRecord<String>{

	public PascalStringTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected String parseDataFrom(byte[] data) {
		final int numBytesInLengthPortion;
		if(this.getRecordLength() <16){
			
		}
		return Ab1Util.parsePascalStringFrom(data);
	}

}
