package org.jcvi.trace.sanger.chromatogram.ab1.tag;

public class ASCIITaggedDataRecord extends AbstractTaggedDataRecord<String>{

	public ASCIITaggedDataRecord(TaggedDataName name, long number,
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
