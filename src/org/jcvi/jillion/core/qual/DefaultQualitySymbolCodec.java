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
/*
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * {@code DefaultQualitySymbolCodec} stores 
 * all the quality values in a byte array
 * one byte per quality value.
 * @author dkatzel
 *
 */
enum DefaultQualitySymbolCodec implements QualitySymbolCodec{

	INSTANCE
	;
	

    @Override
    public PhredQuality decode(byte[] encodedGlyphs, long index) {
        return PhredQuality.valueOf(encodedGlyphs[(int)index]);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length;
    }

    @Override
    public byte[] encode(Collection<PhredQuality> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size());
        for(PhredQuality g : glyphs){
            buf.put(g.getQualityScore());
        }
        return buf.array();
    }

	@Override
	public byte[] toQualityValueArray(byte[] encodedData) {
		byte[] decoded = new byte[encodedData.length];
		System.arraycopy(encodedData, 0, decoded, 0, decoded.length);
		return decoded;
	}

	@Override
	public OptionalDouble getAvgQuality(byte[] encodedData) {
	
		if(encodedData.length==0){
			return OptionalDouble.empty();
		}
		long sum = 0;
		for(int i=0; i<encodedData.length; i++){
			sum +=encodedData[i];
		}
		return OptionalDouble.of(sum/ (double) encodedData.length);
	}

	@Override
	public Optional<PhredQuality> getMinQuality(byte[] encodedData) {
		if(encodedData.length ==0){
			return Optional.empty();
		}
		byte min = PhredQuality.MAX_VALUE;
		
		for(int i=0; i<encodedData.length; i++){
			byte current = encodedData[i];
			if(current < min){
				min = current;
			}
		}
		return Optional.of(PhredQuality.valueOf(min));
	}

	@Override
	public Optional<PhredQuality> getMaxQuality(byte[] encodedData) {
		if(encodedData.length ==0){
			return Optional.empty();
		}
		byte max = PhredQuality.MIN_VALUE;
		
		for(int i=0; i<encodedData.length; i++){
			byte current = encodedData[i];
			if(current > max){
				max = current;
			}
		}
		return Optional.of(PhredQuality.valueOf(max));
	}
    
    
}
