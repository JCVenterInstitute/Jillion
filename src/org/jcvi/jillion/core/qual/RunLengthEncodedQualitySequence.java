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

import java.nio.ByteBuffer;
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
		if(getLength() !=other.getLength()){
			return false;
		}
		Iterator<PhredQuality> iter = iterator();
		Iterator<PhredQuality> otherIter = other.iterator();
		while(iter.hasNext()){
			PhredQuality next = iter.next();
			PhredQuality otherNext = otherIter.next();
			if(!next.equals(otherNext)){
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder((int)getLength());
		Iterator<PhredQuality> iter = iterator();
        while(iter.hasNext()){
        	if(builder.length()>0){
    			builder.append(" ,");
    		}
        	builder.append(iter.next());
        }
		return builder.toString();
	}
	/**
	 * {@inheritDoc}
	 */
	public byte[] toArray(){
        ByteBuffer buf = ByteBuffer.allocate((int)getLength());
        for(PhredQuality quality : this){
            buf.put(quality.getQualityScore());
        }
        return buf.array();
    }

	
}
