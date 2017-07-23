/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.qual;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalDouble;

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
		if(hash==0 && getLength() >0){	        
	        hash= Arrays.hashCode(toArray());
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
	@Override
    public byte[] toArray(){
		return RunLengthEncodedQualityCodec.INSTANCE.toQualityValueArray(encodedData);
        
    }

	@Override
	public OptionalDouble getAvgQuality() {
		return RunLengthEncodedQualityCodec.INSTANCE.getAvgQuality(encodedData);
	}


	@Override
	public Optional<PhredQuality> getMinQuality() {
		return RunLengthEncodedQualityCodec.INSTANCE.getMinQuality(encodedData);
	}


	@Override
	public Optional<PhredQuality> getMaxQuality() {
		return RunLengthEncodedQualityCodec.INSTANCE.getMaxQuality(encodedData);
	}
}
