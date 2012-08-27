package org.jcvi.common.core.symbol.qual;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.common.core.Range;

class RunLengthEncodedQualitySequence implements QualitySequence{
	private final byte[] encodedData;
	
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
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(encodedData);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RunLengthEncodedQualitySequence)) {
			return false;
		}
		//guard value should always be the same?
		//so we can just do an array equality check
		RunLengthEncodedQualitySequence other = (RunLengthEncodedQualitySequence) obj;
		if (!Arrays.equals(encodedData, other.encodedData)) {
			return false;
		}
		return true;
	}

	

	
}
