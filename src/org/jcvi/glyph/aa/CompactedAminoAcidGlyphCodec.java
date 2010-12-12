/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.glyph.aa;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.IOUtil;

public class CompactedAminoAcidGlyphCodec implements GlyphCodec<AminoAcid>{

	 /**
     * The header will contain an int value specifying how many glyphs are encoded.
     */
    private static final int HEADER_LENGTH = 4;
    
    private static final int BITS_PER_GLYPH = 5;
    
    private static final int GLYPHS_PER_BYTE_GROUP = 8;
    
    private static final int SIZE_OF_BYTE_GROUP = BITS_PER_GLYPH*GLYPHS_PER_BYTE_GROUP;
    private static final BigInteger MASK = new BigInteger("1F",16);
    
	@Override
	public List<AminoAcid> decode(byte[] encodedGlyphs) {
		ByteBuffer buffer = ByteBuffer.wrap(encodedGlyphs);
		int size = buffer.getInt();
		List<AminoAcid> decoded = new ArrayList<AminoAcid>(size);
		int numberOfGroups = size/SIZE_OF_BYTE_GROUP;
		for(int i=0; i< numberOfGroups-1; i++){
			byte[] group = getNextByteGroup(buffer);
			decoded.addAll(decodeGroup(group));
		}
		//final group might not be filled
		int partialSize = size % GLYPHS_PER_BYTE_GROUP;
		byte[] group = getNextByteGroup(buffer,partialSize);
		decoded.addAll(decodeGroup(group, partialSize));
		return decoded;
	}
	private byte[] getNextByteGroup(ByteBuffer buffer){
		return getNextByteGroup(buffer, GLYPHS_PER_BYTE_GROUP);
	}
	private byte[] getNextByteGroup(ByteBuffer buffer, int numberOfGlyphs) {
		byte[] group = new byte[numberOfGlyphs];
		buffer.get(group);
		return group;
	}
	private List<AminoAcid> decodeGroup(byte[] groupBytes){
		return decodeGroup(groupBytes, GLYPHS_PER_BYTE_GROUP);
	}
	private List<AminoAcid> decodeGroup(byte[] groupBytes, int numberOfGlyphs){
		List<AminoAcid> group = new ArrayList<AminoAcid>(numberOfGlyphs);
		BigInteger temp = new BigInteger(1,groupBytes);
		System.out.println("decoded group value ="+temp);
		for(int i=0; i< numberOfGlyphs; i++){
			int index = temp.and(MASK).intValue();
			System.out.println("decoding "+ index);
			group.add(AminoAcid.values()[index]);
			temp =temp.shiftRight(BITS_PER_GLYPH);
		}
		return group;
	}
	@Override
	public AminoAcid decode(byte[] encodedGlyphs, int index) {
		// TODO optimize
		return decode(encodedGlyphs).get(index);
	}

	@Override
	public int decodedLengthOf(byte[] encodedGlyphs) {
		return ByteBuffer.wrap(encodedGlyphs).getInt();
	}

	@Override
	public byte[] encode(Collection<AminoAcid> glyphs) {
		 final int unEncodedSize = glyphs.size();
        
        int encodedSize = computeEncodedSize(unEncodedSize);
        return encodeGlyphs(glyphs, unEncodedSize, encodedSize);
	}

	 private int computeEncodedSize(final int unEncodedSize) {
		 
        int encodedSize = HEADER_LENGTH + BITS_PER_GLYPH *(1 +unEncodedSize/GLYPHS_PER_BYTE_GROUP);
        return encodedSize;
    }
	 private byte[] encodeGlyphs(Collection<AminoAcid> glyphs,
	            final int unEncodedSize, int encodedSize) {
        ByteBuffer result = ByteBuffer.allocate(encodedSize);
        result.putInt(unEncodedSize);
        Iterator<AminoAcid> iterator = glyphs.iterator();
        encodeAllButTheLastByteGroup(iterator, unEncodedSize, result);
        return result.array();
    }
	 /**
	     * pack every 2 glyphs into a single byte.  this method
	     * encodes all glyphs upto but not including the final byte
	     * since the final byte is a special case.
	     * @param glyphs
	     * @param unEncodedSize
	     * @param result
	     */
	    private void encodeAllButTheLastByteGroup(Iterator<AminoAcid> glyphs,
	            final int unEncodedSize, ByteBuffer result) {
	        for(int i=0; i<unEncodedSize-GLYPHS_PER_BYTE_GROUP; i+=GLYPHS_PER_BYTE_GROUP){
	            encodeNextGroup(glyphs, result);
	        }
	    }
	    
	    private void encodeNextGroup(Iterator<AminoAcid> glyphs, ByteBuffer result) {
	        long byteGroupValue=0;
	        for(int i=0; i< GLYPHS_PER_BYTE_GROUP; i++){
	        	if(glyphs.hasNext()){
		        	int ordinal = glyphs.next().ordinal();
		        	System.out.println("encoding "+ ordinal);
					byteGroupValue = byteGroupValue | ordinal;
		        	byteGroupValue = byteGroupValue<<BITS_PER_GLYPH;
	        	}
	        }
	        System.out.println("encoded group value ="+byteGroupValue);
	    	for(int i=0 ;i<GLYPHS_PER_BYTE_GROUP; i++){
	    		result.put((byte) ((byteGroupValue>>>(i *BITS_PER_GLYPH)) &0xFF));
	    	}
	        
	    }
}
