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
package org.jcvi.jillion.core.residue.aa;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.GlyphCodec;
/**
 * {@code CompactAminoAcidSequenceCodec} is able to store
 * {@link AminoAcid} in a byte array where each {@link AminoAcid}
 * only takes up 5 bits. This is a 37.5% memory reduction compared to 
 * encoding the data as one byte each or 68% memory reduction compared
 * to encoding each AminoAcid as one char each.
 * @author dkatzel
 *
 */
public enum CompactAminoAcidSequenceCodec implements GlyphCodec<AminoAcid> {
	/**
	 * Singleton instance.
	 */
	INSTANCE
	;
	private static final int BITS_PER_AA = 5;
	
	private CompactAminoAcidSequenceCodec(){
		//private constructor
	}
	@Override
	public byte[] encode(Collection<AminoAcid> glyphs) {
		int numberOfAminoAcids = glyphs.size();
		int numBits = numberOfAminoAcids * BITS_PER_AA;
		BitSet bits = new BitSet(numBits);
		int offset=0;
		for(AminoAcid aa : glyphs){
			byte ordinal = aa.getOrdinalAsByte();
			
			for(int i=0; i<BITS_PER_AA; i++){
				if((ordinal & (1<< i))!=0){
					bits.set(offset);
				}
				offset++;
			}
		}
		byte[] encodedData = IOUtil.toByteArray(bits,numBits);
		ByteBuffer buf = ByteBuffer.allocate(4 + encodedData.length);
		buf.putInt(numberOfAminoAcids);
		buf.put(encodedData);
		return buf.array();
	}


	protected AminoAcid getAminoAcidFor(BitSet subSet) {
		final AminoAcid aa;
		if(subSet.isEmpty()){
			aa =AminoAcid.values()[0];
		}else{
			aa =AminoAcid.values()[new BigInteger(IOUtil.toByteArray(subSet,BITS_PER_AA)).intValue()];
		}
		return aa;
	}

	@Override
	public AminoAcid decode(byte[] encodedGlyphs, long index) {
		byte[] tmp = Arrays.copyOfRange(encodedGlyphs, 4, encodedGlyphs.length);
		
		BitSet bits = IOUtil.toBitSet(tmp);
		int bitOffset = BITS_PER_AA *(int)index;
		BitSet subSet = bits.get(bitOffset, bitOffset+BITS_PER_AA);
		return getAminoAcidFor(subSet);
	}

	@Override
	public int decodedLengthOf(byte[] encodedGlyphs) {
		ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
		return buf.getInt();
	}

}
