package org.jcvi.trace.sanger.chromatogram.abi.tag;

public class DefaultAsciiTaggedDataRecord extends AbstractTaggedDataRecord<String>{

	public DefaultAsciiTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	@Override
	protected String parseDataFrom(byte[] data) {
		return new String(data);
	}


}
