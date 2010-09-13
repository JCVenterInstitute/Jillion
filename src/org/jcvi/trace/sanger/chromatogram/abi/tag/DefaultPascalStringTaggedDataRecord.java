package org.jcvi.trace.sanger.chromatogram.abi.tag;

import org.jcvi.trace.sanger.chromatogram.abi.Ab1Util;

public class DefaultPascalStringTaggedDataRecord extends AbstractTaggedDataRecord<String>{

	public DefaultPascalStringTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected String parseDataFrom(byte[] data) {		
		return Ab1Util.parsePascalStringFrom(data);
	}

}
