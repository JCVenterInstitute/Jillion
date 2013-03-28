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
/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.GlyphCodec;
/**
 * <code>DefaultNucleotideGlyphCodec</code> is the implementation
 * of {@link GlyphCodec} that can converts {@link Nucleotide}s
 * into a 4 bit representation.  This allows 2 {@link Nucleotide}s
 * to be packed into a single byte.
 * @author dkatzel
 *
 *
 */
public enum DefaultNucleotideCodec implements NucleotideCodec{
    /**
     * Singleton instance.
     */
    INSTANCE;
    /**
     * Maintains the mapping of each glyph singleton with
     * its assigned glyphcode value.  The glyphcodes have been specially
     * set to simplify reverse complementing.
     */
    private static final Map<Byte, Nucleotide> BYTE_TO_GLYPH_MAP = new HashMap<Byte, Nucleotide>();
    /**
     * Maintains the mapping of each glyph singleton with
     * its assigned glyphcode value.  The glyphcodes have been specially
     * set to simplify reverse complementing.
     */
    private static final Map<Nucleotide, Byte> GLYPH_TO_BYTE_MAP = new EnumMap<Nucleotide, Byte>(Nucleotide.class);
    /**
     * The header will contain an int value specifying how many glyphs are encoded.
     */
    private static final int HEADER_LENGTH = 4;
    
    /**
     * The header will contain an int value specifying how many glyphs are encoded.
     */
    private static final int BITS_PER_GLYPH = 4;
    
    private static final Nucleotide[] ORDINAL_VALUES = Nucleotide.values();
    
    private final int singleGlyphEncodedSize = computeEncodedSize(1);
    
    
    /**
     * populate the maps.
     * Each byte key has been specially assigned so
     * that flipping the bits (then bit masking)
     * returns the byte value for the key of the reverse complement
     * glyph where applicable.
     */
    static{
        //special case for
        //Gap and Unknown since they complement to themselves
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x00), Nucleotide.Gap);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0F), Nucleotide.Unknown);
        //everything else has a complement
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x01), Nucleotide.Adenine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0E), Nucleotide.Thymine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x02), Nucleotide.Guanine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0D), Nucleotide.Cytosine);

        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x03), Nucleotide.Pyrimidine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0C), Nucleotide.Purine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x04), Nucleotide.Weak);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0B), Nucleotide.Strong);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x05), Nucleotide.Keto);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0A), Nucleotide.Amino);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x06), Nucleotide.NotCytosine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x09), Nucleotide.NotGuanine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x07), Nucleotide.NotThymine);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x08), Nucleotide.NotAdenine);
        //populate the reverse mapping
        for(Entry<Byte, Nucleotide> entry : BYTE_TO_GLYPH_MAP.entrySet()){
            GLYPH_TO_BYTE_MAP.put(entry.getValue(), entry.getKey());           
        }
    }
   
    @Override
    public Nucleotide decode(byte[] encodedGlyphs, long index){
        final byte getByteForGlyph = getEncodedByteForGlyph(encodedGlyphs,index);
        return decode(getByteForGlyph, isEven(index));
    }
    private Nucleotide decode(final byte getByteForGlyph, boolean isFirstNibble) {
        byte[] next2 = decodeNext2Values(getByteForGlyph);
        if(isFirstNibble){
            return ORDINAL_VALUES[next2[0]];
        }
        return ORDINAL_VALUES[next2[1]];
    }
    private byte getEncodedByteForGlyph(byte[] encodedGlyphs, long index) {
        final int encodedIndex = computeEncodedIndexForGlyph(index);
        if(encodedIndex >= encodedGlyphs.length){
            throw new IndexOutOfBoundsException("index "+index + " corresponds to encodedIndex "+encodedIndex + "  encodedglyph length is "+encodedGlyphs.length);
        }
        return  encodedGlyphs[encodedIndex];
    }
    private int computeEncodedIndexForGlyph(long index) {
        if(index<0){
            throw new IndexOutOfBoundsException("index can not be negative: "+index);
        }
        return (int)(HEADER_LENGTH+index/2);
    }
    @Override
    public byte[] encode(int numberOfNucleotides,Iterator<Nucleotide> nucleotides) {        
        int encodedSize = computeEncodedSize(numberOfNucleotides);
        return encodeGlyphs(nucleotides, numberOfNucleotides, encodedSize);
        
    }
    @Override
    public byte[] encode(Collection<Nucleotide> glyphs) {
        final int unEncodedSize = glyphs.size();
        
        int encodedSize = computeEncodedSize(unEncodedSize);
        return encodeGlyphs(glyphs.iterator(), unEncodedSize, encodedSize);
        
    }
    /**
     * Convenience method to encode a single basecall.
     * @param glyph
     * @return
     */
    @Override
    public byte[] encode(Nucleotide glyph) {
        ByteBuffer result = ByteBuffer.allocate(singleGlyphEncodedSize);
        result.putInt(1);
        encodeLastValue(glyph, result);
        return result.array();
        
    }
    private byte[] encodeGlyphs(Iterator<Nucleotide> iterator,
            final int unEncodedSize, int encodedSize) {
        ByteBuffer result = ByteBuffer.allocate(encodedSize);
        result.putInt(unEncodedSize);
        encodeAllButTheLastByte(iterator, unEncodedSize, result);
        encodeFinalByte(iterator, unEncodedSize, result);
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
    private void encodeAllButTheLastByte(Iterator<Nucleotide> glyphs,
            final int unEncodedSize, ByteBuffer result) {
        for(int i=0; i<unEncodedSize-2; i+=2){
            encodeNext2Values(glyphs, result);
        }
    }
    /**
     * the final encoded byte is a special case because
     * there may only be a single glyph inside if
     * the unencoded size is odd.
     * @param glyphs
     * @param unEncodedSize
     * @param result
     */
    private void encodeFinalByte(Iterator<Nucleotide> glyphs,
            final int unEncodedSize, ByteBuffer result) {
        if(unEncodedSize>0){
            final boolean even = isEven(unEncodedSize);
            if(even){
                encodeNext2Values(glyphs, result);
            }
            else{
                encodeLastValue(glyphs.next(), result);
            }
        }
    }
    private int computeEncodedSize(final int size) {
        return HEADER_LENGTH + size/2 + (isEven(size)?0:1);
    }
    private boolean isEven(final long size) {
        return size%2==0;
    }
    private void encodeLastValue(Nucleotide glyph, ByteBuffer result) {
        byte hi = GLYPH_TO_BYTE_MAP.get(glyph);
        result.put((byte) ((hi<<BITS_PER_GLYPH) &0xFF));
    }
    private void encodeNext2Values(Iterator<Nucleotide> glyphs, ByteBuffer result) {
        byte hi = GLYPH_TO_BYTE_MAP.get(glyphs.next());
        byte low = GLYPH_TO_BYTE_MAP.get(glyphs.next());
        result.put((byte) ((hi<<BITS_PER_GLYPH | low) &0xFF));
    }
    private byte[] decodeNext2Values(byte b) {
        byte hi = (byte)(b>>>BITS_PER_GLYPH &0x0F);
        byte low = (byte)(b & 0x0F);
       return new byte[]{BYTE_TO_GLYPH_MAP.get(hi).getOrdinalAsByte(),BYTE_TO_GLYPH_MAP.get(low).getOrdinalAsByte()};
    }
    
    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        return buf.getInt();
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public List<Integer> getGapOffsets(byte[] encodedGlyphs) {
    	Iterator<Nucleotide> iter = iterator(encodedGlyphs);
    	List<Integer> gaps = new ArrayList<Integer>();
    	int i=0;
    	while(iter.hasNext()){
    		if(iter.next() == Nucleotide.Gap){
    			gaps.add(Integer.valueOf(i));
    		}
    		i++;
    	}
        return gaps;
    }
 
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps(byte[] encodedGlyphs) {
    	Iterator<Nucleotide> iter = iterator(encodedGlyphs);
    	
    	int numGaps=0;
    	while(iter.hasNext()){
    		if(iter.next() == Nucleotide.Gap){
    			numGaps++;
    		}
    	}
    	return numGaps;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
        return getGapOffsets(encodedGlyphs).contains(Integer.valueOf(gappedOffset));
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getUngappedLength(byte[] encodedGlyphs) {
      return decodedLengthOf(encodedGlyphs) - getNumberOfGaps(encodedGlyphs);
    }
   
   
  
    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
        return gappedOffset - getNumberOfGapsUntil(encodedGlyphs,gappedOffset);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(byte[] encodedGlyphs, int ungappedOffset) {
      
    	int currentGappedOffset=0;
    	int currentUngappedOffset=0;
    	Iterator<Nucleotide> iter = iterator(encodedGlyphs);
    	while(iter.hasNext() && ungappedOffset<currentUngappedOffset){
    		if(iter.next()!=Nucleotide.Gap){
    			currentUngappedOffset++;
    		}
    		currentGappedOffset++;
    	}
        
        return currentGappedOffset;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
        int numberOfGaps=0;
        for(Integer gapIndex :getGapOffsets(encodedGlyphs)){
            if(gapIndex.intValue() <=gappedOffset){
                numberOfGaps++;
            }
        }
        return numberOfGaps;
    }
	@Override
	public Iterator<Nucleotide> iterator(byte[] encodedGlyphs) {		
		return new IteratorImpl(encodedGlyphs);
	}
	
	@Override
	public Iterator<Nucleotide> iterator(byte[] encodedData, Range range) {
		return new IteratorImpl(encodedData, range);
	}
	@Override
	public String toString(byte[] encodedData) {
		
		int length = decodedLengthOf(encodedData);
		if(length==0){
			return "";
		}
		int currentOffset=0;
		StringBuilder builder = new StringBuilder(length);
		byte encodedByte;
		byte[] currentDecodedBytes ;
		while(currentOffset < length-2){
			encodedByte = encodedData[computeEncodedIndexForGlyph(currentOffset)];
			currentDecodedBytes = decodeNext2Values(encodedByte);
			builder.append(ORDINAL_VALUES[currentDecodedBytes[0]]);
			builder.append(ORDINAL_VALUES[currentDecodedBytes[1]]);
			currentOffset+=2;
		}
		encodedByte = encodedData[computeEncodedIndexForGlyph(currentOffset)];
		currentDecodedBytes = decodeNext2Values(encodedByte);
		builder.append(ORDINAL_VALUES[currentDecodedBytes[0]]);
		if(isEven(length)){
			builder.append(ORDINAL_VALUES[currentDecodedBytes[1]]);
		}
		return builder.toString();
	}

	private class IteratorImpl implements Iterator<Nucleotide>{
		private final byte[] encodedData;
		private final int length;
		private int currentOffset=0;
		private byte[] currentDecodedBytes;
		
		public IteratorImpl(byte[] encodedData, Range range) {
			this.encodedData = encodedData;
			int seqLength = decodedLengthOf(encodedData);
			if(range.getBegin()<0 || range.getEnd()>=seqLength){
				throw new IndexOutOfBoundsException("range "+range +" is out of range of sequence which is only "+ new Range.Builder(seqLength).build());
			}
			currentOffset = (int)range.getBegin();
			length = (int)range.getEnd()+1;
			if(hasNext()){
				byte encodedByte = encodedData[computeEncodedIndexForGlyph(currentOffset)];
				currentDecodedBytes = decodeNext2Values(encodedByte);
			}
		}
		public IteratorImpl(byte[] encodedData) {
			this.encodedData = encodedData;
			this.length = decodedLengthOf(encodedData);
			if(hasNext()){
				byte encodedByte = encodedData[computeEncodedIndexForGlyph(0)];
				currentDecodedBytes = decodeNext2Values(encodedByte);
			}
		}

		@Override
		public boolean hasNext() {
			return currentOffset <length;
		}

		@Override
		public Nucleotide next() {
			if(!hasNext()){
				throw new NoSuchElementException("no more elements");
			}
			final Nucleotide ret;
			if(isEven(currentOffset)){
				ret= ORDINAL_VALUES[currentDecodedBytes[0]];
			}else{
				ret = ORDINAL_VALUES[currentDecodedBytes[1]];
			}
			currentOffset++;
			if(isEven(currentOffset) && hasNext()){
				byte encodedByte = encodedData[computeEncodedIndexForGlyph(currentOffset)];
				currentDecodedBytes = decodeNext2Values(encodedByte);
			}
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("can not modify immutable sequence");
			
		}
		
	}
}
