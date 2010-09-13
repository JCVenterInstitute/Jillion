package org.jcvi.trace.sanger.chromatogram.abi.tag;


public interface TaggedDataRecord<T> {
	
	TaggedDataName getTagName();
	long getTagNumber();
	TaggedDataType getDataType();
	int getElementLength();
	long getNumberOfElements();
	long getRecordLength();
	long getDataRecord();
	long getCrypticValue();
	
	T parseDataRecordFrom(byte[] ab1DataBlock);
}
