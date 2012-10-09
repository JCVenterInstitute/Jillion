package org.jcvi.common.core.symbol.qual;

import java.util.Iterator;

import org.jcvi.common.core.Range;

class RunLengthEncodedQualitySequence implements QualitySequence{
	private final byte[] encodedData;
	private int hash;
	public RunLengthEncodedQualitySequence(byte[] encodedData) {
		this.encodedData = encodedData;
	}

	@Override
	public PhredQuality get(long index) {
		return RunLengthEncodedQualityCodec.INSTANCE.decode(encodedData, index);
	}

	@Override
	public long getLength() {
		return RunLengthEncodedQualityCodec.INSTANCE.decodedLengthOf(encodedData);
	}

	@Override
	public Iterator<PhredQuality> iterator(Range range) {
		return RunLengthEncodedQualityCodec.INSTANCE.iterator(encodedData,range);
	}

	@Override
	public Iterator<PhredQuality> iterator() {
		return RunLengthEncodedQualityCodec.INSTANCE.iterator(encodedData);
	}

	@Override
	public int hashCode() {
		long length = getLength();
		if(hash==0 && length >0){
	        final int prime = 31;
	        int result = 1;
	        Iterator<PhredQuality> iter = iterator();
	        while(iter.hasNext()){
	        	result = prime * result + iter.next().hashCode();
	        }
	        hash= result;
		}
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof QualitySequence)) {
			return false;
		}
		//guard value should always be the same?
		//so we can just do an array equality check
		QualitySequence other = (QualitySequence) obj;
		if(getLength() !=other.getLength()){
			return false;
		}
		Iterator<PhredQuality> iter = iterator();
		Iterator<PhredQuality> otherIter = other.iterator();
		while(iter.hasNext()){
			if(!iter.next().equals(otherIter.next())){
				return false;
			}
		}
		return true;
	}

	

	
}
