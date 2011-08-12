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
/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.symbol.GlyphCodec;
/**
 * <code>DefaultNucleotideGlyphCodec</code> is the implementation
 * of {@link GlyphCodec} that can converts {@link Nucleotide}s
 * into a 4 bit representation.  This allows 2 {@link Nucleotide}s
 * to be packed into a single byte.
 * @author dkatzel
 *
 *
 */
public enum DefaultNucleotideGlyphCodec implements NucleotideCodec{
    /**
     * Singleton instance.
     */
    INSTANCE;
    /**
     * Maintains the mapping of each glyph singleton with
     * its assigned glyphcode value.  The glyphcodes have been specially
     * set to simplify reverse complimenting.
     */
    private static final Map<Byte, Nucleotide> BYTE_TO_GLYPH_MAP = new HashMap<Byte, Nucleotide>();
    /**
     * Maintains the mapping of each glyph singleton with
     * its assigned glyphcode value.  The glyphcodes have been specially
     * set to simplify reverse complimenting.
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
    
    private final int singleGlyphEncodedSize = computeEncodedSize(1);
    /**
     * populate the maps.
     * Each byte key has been specially assigned so
     * that flipping the bits (then bit masking)
     * returns the byte value for the key of the reverse compliment
     * glyph where applicable.
     */
    static{
        //special case for
        //Gap and Unknown since they compliment to themselves
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x00), Nucleotide.Gap);
        BYTE_TO_GLYPH_MAP.put(Byte.valueOf((byte)0x0F), Nucleotide.Unknown);
        //everything else has a compliment
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
    public List<Nucleotide> decode(byte[] encodedGlyphs) {
        int length = decodedLengthOf(encodedGlyphs);
        List<Nucleotide> result = new ArrayList<Nucleotide>(length);
        
        for(int i=HEADER_LENGTH; i<encodedGlyphs.length-1; i++){
            result.addAll(decodeNext2Values(encodedGlyphs[i]));
        }
        if(length>0){
            if(isEven(length)){
                result.addAll(decodeNext2Values(encodedGlyphs[encodedGlyphs.length-1]));
            }
            else{
                result.add(decodeLastValues(encodedGlyphs[encodedGlyphs.length-1]));
            }
        }
        return result;
    }
    @Override
    public Nucleotide decode(byte[] encodedGlyphs, int index){
        final byte getByteForGlyph = getEncodedByteForGlyph(encodedGlyphs,index);
        return decode(getByteForGlyph, isEven(index));
    }
    private Nucleotide decode(final byte getByteForGlyph, boolean isFirstNibble) {
        List<Nucleotide> values = decodeNext2Values(getByteForGlyph);
        if(isFirstNibble){
            return values.get(0);
        }
        return values.get(1);
    }
    private byte getEncodedByteForGlyph(byte[] encodedGlyphs, int index) {
        final int encodedIndex = computeEncodedIndexForGlyph(index);
        if(encodedIndex >= encodedGlyphs.length){
            throw new ArrayIndexOutOfBoundsException("index "+index + " corresponds to encodedIndex "+encodedIndex + "  encodedglyph length is "+encodedGlyphs.length);
        }
        final byte getByteForGlyph = encodedGlyphs[encodedIndex];
        return getByteForGlyph;
    }
    private int computeEncodedIndexForGlyph(int index) {
        if(index<0){
            throw new IllegalArgumentException("index can not be negative: "+index);
        }
        final int encodedIndexForGlyph = HEADER_LENGTH+index/2;
        return encodedIndexForGlyph;
    }

    @Override
    public byte[] encode(Collection<Nucleotide> glyphs) {
        final int unEncodedSize = glyphs.size();
        
        int encodedSize = computeEncodedSize(unEncodedSize);
        return encodeGlyphs(glyphs, unEncodedSize, encodedSize);
        
    }
    /**
     * Convenience method to encode a single basecall.
     * @param glyph
     * @return
     */
    public byte[] encode(Nucleotide glyph) {
        ByteBuffer result = ByteBuffer.allocate(singleGlyphEncodedSize);
        result.putInt(1);
        encodeLastValue(glyph, result);
        return result.array();
        
    }
    private byte[] encodeGlyphs(Collection<Nucleotide> glyphs,
            final int unEncodedSize, int encodedSize) {
        ByteBuffer result = ByteBuffer.allocate(encodedSize);
        result.putInt(unEncodedSize);
        Iterator<Nucleotide> iterator = glyphs.iterator();
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
        int encodedSize = HEADER_LENGTH + size/2 + (isEven(size)?0:1);
        return encodedSize;
    }
    private boolean isEven(final int size) {
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
    private List<Nucleotide> decodeNext2Values(byte b) {
        byte hi = (byte)(b>>>BITS_PER_GLYPH &0x0F);
        byte low = (byte)(b & 0x0F);
       return Arrays.asList(BYTE_TO_GLYPH_MAP.get(hi),BYTE_TO_GLYPH_MAP.get(low));
    }
    private Nucleotide decodeLastValues(byte b) {
        byte hi = (byte)(b>>>BITS_PER_GLYPH &0x0F);
       return BYTE_TO_GLYPH_MAP.get(hi);
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
        List<Nucleotide> gapped =decode(encodedGlyphs);
        return getGapOffsets(gapped);
    }
    private List<Integer> getGapOffsets(List<Nucleotide> gapped) {
        List<Integer> result = new ArrayList<Integer>();
        for(int i=0; i<gapped.size(); i++){
            if(gapped.get(i)==Nucleotide.Gap){
                result.add(Integer.valueOf(i));
            }
        }       
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps(byte[] encodedGlyphs) {
        return getGapOffsets(encodedGlyphs).size();
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
        List<Nucleotide> gapped =decode(encodedGlyphs);
        return gapped.size() -getGapOffsets(gapped).size();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public List<Nucleotide> asUngappedList(byte[] encodedGlyphs) {
        List<Nucleotide> gapped =decode(encodedGlyphs);
        List<Integer> gapOffsets = getGapOffsets(gapped);
        Collections.reverse(gapOffsets);
        for(Integer offset : gapOffsets){
            gapped.remove(offset.intValue());
        }
        return gapped;
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
       /*
        *  int[] gaps =getGapOffsetsFrom(encodedGlyphs);
        int currentOffset=ungappedOffset;
        for(int i=0; i<gaps.length && gaps[i]>currentOffset; i++){
            currentOffset++;
        }
        return currentOffset;
        */
        List<Integer> gaps = getGapOffsets(encodedGlyphs);
        int currentOffset=ungappedOffset;
        for(int i=0; i<gaps.size() && gaps.get(i).intValue()>currentOffset; i++){
            currentOffset++;
        }
        return currentOffset;
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
}
