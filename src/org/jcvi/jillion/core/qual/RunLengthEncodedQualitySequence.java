/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.qual;

import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;

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
		return Arrays.equals(toArray(), other.toArray());
		
	}

	@Override
	public String toString() {
		byte[] values = toArray();
		//buffer is length*5 to account for ' ,Q<2digitvalue>'
		StringBuilder builder = new StringBuilder(values.length*5);
		int lastOffset = values.length-1;
		for(int i=0; i<lastOffset; i++){
			builder.append(PhredQuality.toString(values[i])).append(" ,");
		}
       //last value doesn't get a trailing comma
		builder.append(PhredQuality.toString(values[lastOffset]));
		return builder.toString();
	}
	/**
	 * {@inheritDoc}
	 */
	public byte[] toArray(){
		return RunLengthEncodedQualityCodec.INSTANCE.toQualityValueArray(encodedData);
        
    }

	
}
