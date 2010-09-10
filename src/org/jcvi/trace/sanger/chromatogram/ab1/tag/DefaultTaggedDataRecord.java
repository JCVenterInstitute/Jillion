package org.jcvi.trace.sanger.chromatogram.ab1.tag;

public class DefaultTaggedDataRecord extends AbstractTaggedDataRecord<byte[]>{

	public DefaultTaggedDataRecord(TaggedDataName name, long number,
			TaggedDataType dataType, int elementLength, long numberOfElements,
			long recordLength, long dataRecord, long crypticValue) {
		super(name, number, dataType, elementLength, numberOfElements, recordLength,
				dataRecord, crypticValue);
	}

	/* (non-Javadoc)
	 * @see org.jcvi.trace.sanger.chromatogram.ab1.tag.AbstractTaggedDataRecord#parseDataFrom(byte[])
	 */
	@Override
	protected byte[] parseDataFrom(byte[] data) {

		return data;
	}

	

}
