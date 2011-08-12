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

package org.jcvi.common.core.symbol.residue.nuc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.DeltaEncoder;
import org.jcvi.common.core.symbol.GlyphCodec;

/**
 * {@code TwoBitEncodedNucleotideCodec} is a {@link GlyphCodec}
 * of Nucletotides that can encode a list of {@link Nucleotide}s
 * that only contain A,C,G,T and gaps (no ambiguities) in as little as 2 bits per base
 * plus some extra bytes for storing the gaps. This should 
 * greatly reduce the memory footprint of most kinds of read data.
 * @author dkatzel
 *
 *
 */
public enum NoAmbiguitiesEncodedNucleotideCodec implements NucleotideCodec{
    /**
     * Singleton instance.
     */
    INSTANCE
    ;
    static boolean canEncode(Iterable<Nucleotide> nucleotides){
        for(Nucleotide n :nucleotides){
            if(n.isAmbiguity()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * The header will contain an int value specifying how many glyphs are encoded.
     */
    private static final int HEADER_LENGTH = 8;
    
    
    private static final int NUCLEOTIDES_PER_BYTE =4;

    private static final int UNSIGNED_BYTE_MAX = 255;
    
    private static final int UNSIGNED_SHORT_MAX = 65531;
    /**
     * This is a sentinel value for a gap.  Since we 
     * can only store 2 bits per base, a byte of 5 is too big.
     * 
     */
    private static final byte GAP_BYTE = 5;
    
    /**
     * We can compress our data more if the length
     * is small enough that any possible
     * gap index will fit in only 1/2 or 4 bytes.
     * @param length the length of the nucleotide sequence
     * to encode.
     * @return 1 2 or 4 depending on how many
     * bytes are required to store each offset for the length.
     */
    private int computeBytesPerGapOffset(int length){
        if(length<= UNSIGNED_BYTE_MAX){
            return 1;
        }
        if(length<= UNSIGNED_SHORT_MAX){
            return 2;
        }
        return 4;
    }
    @Override
    public List<Nucleotide> decode(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int length = decodedLengthOf(encodedGlyphs);
        int numberOfBytesPerGap = computeBytesPerGapOffset(length);
        int[] gaps = getGapOffsets(buf,numberOfBytesPerGap);
        List<Nucleotide> result = decodeNucleotidesWithGapSentientals(
                encodedGlyphs, length, numberOfBytesPerGap, gaps);
        for(int i=0; i<gaps.length; i++){
            int gapOffset = gaps[i];
            //we had to put something in the gap
            //location as a place holder so get rid of it
            result.remove(gapOffset);
            result.add(gapOffset, Nucleotide.Gap);
        }
        
        return result;
    }
    private List<Nucleotide> decodeNucleotidesWithGapSentientals(
            byte[] encodedGlyphs, int length, int numberOfBytesPerGap,
            int[] gaps) {
        List<Nucleotide> result = new ArrayList<Nucleotide>(length);
        
        int startOfEncodedBases = HEADER_LENGTH+numberOfBytesPerGap*gaps.length;
        for(int i=startOfEncodedBases; i<encodedGlyphs.length-1; i++){
            result.addAll(decodeNucleotidesIn(encodedGlyphs[i]));
        }
        if(length>0){
            int remainder = length% NUCLEOTIDES_PER_BYTE;
            List<Nucleotide> lastValues = decodeNucleotidesIn(encodedGlyphs[encodedGlyphs.length-1]);
            if(remainder ==0){
                result.addAll(lastValues);
            }else{
                result.addAll(lastValues.subList(0, remainder));
            }            
        }
        return result;
    }
    private int[] getGapOffsets(ByteBuffer buf, int bytesPerOffset){
        buf.position(4);
        int[] gaps = new int[buf.getInt()];
        
        for(int i=0; i<gaps.length; i++){
            switch(bytesPerOffset){
                case 1 : gaps[i] =IOUtil.convertToUnsignedByte(buf.get());
                        break;
                case 2 : gaps[i] =IOUtil.convertToUnsignedShort(buf.getShort());
                        break; 
                default : gaps[i] =buf.getInt();
                        break;
            }
        }
        return gaps;
    }
    
    private int[] getGapOffsetsFrom(byte[] encodedData){
        int length = decodedLengthOf(encodedData);
        int numberOfBytesPerGap = computeBytesPerGapOffset(length);
       
        int[] gaps = getGapOffsets(ByteBuffer.wrap(encodedData),numberOfBytesPerGap);
      
        return gaps;
    }
    @Override
    public Nucleotide decode(byte[] encodedGlyphs, int index){
        int length = decodedLengthOf(encodedGlyphs);
        int numberOfBytesPerGap = computeBytesPerGapOffset(length);
       
        int[] gaps = getGapOffsets(ByteBuffer.wrap(encodedGlyphs),numberOfBytesPerGap);
        if(Arrays.binarySearch(gaps, index)>=0){
            return Nucleotide.Gap;
        }
        final byte getByteForGlyph = getEncodedByteForGlyph(encodedGlyphs,gaps.length,numberOfBytesPerGap,index);
        return decode(getByteForGlyph, index %NUCLEOTIDES_PER_BYTE);
    }
    private Nucleotide decode(final byte getByteForGlyph, int index) {
        List<Nucleotide> values = decodeNucleotidesIn(getByteForGlyph);
        return values.get(index);
    }
    private byte getEncodedByteForGlyph(byte[] encodedGlyphs, int numberOfGaps,int numberOfBytesPerGap, int index) {
        final int encodedIndex = computeEncodedIndexForGlyph(numberOfGaps,numberOfBytesPerGap,index);
        if(encodedIndex >= encodedGlyphs.length){
            throw new ArrayIndexOutOfBoundsException("index "+index + " corresponds to encodedIndex "+encodedIndex + "  encodedglyph length is "+encodedGlyphs.length);
        }
        final byte getByteForGlyph = encodedGlyphs[encodedIndex];
        return getByteForGlyph;
    }
    private int computeEncodedIndexForGlyph(int numberOfGaps,int numberOfBytesPerGap,int index) {
        if(index<0){
            throw new IllegalArgumentException("index can not be negative: "+index);
        }
        final int encodedIndexForGlyph = HEADER_LENGTH+numberOfBytesPerGap*numberOfGaps+index/4;
        return encodedIndexForGlyph;
    }

    @Override
    public byte[] encode(Collection<Nucleotide> glyphs) {
        final int unEncodedSize = glyphs.size();
        return encodeNucleotides(glyphs, unEncodedSize);
        
    }
    /**
     * Convenience method to encode a single basecall.
     * @param glyph
     * @return
     */
    public byte[] encode(Nucleotide glyph) {
        return encodeNucleotides(Arrays.asList(glyph),1);
        
    }
    private byte[] encodeNucleotides(Collection<Nucleotide> glyphs,
            final int unEncodedSize) {
        int encodedBasesSize = computeHeaderlessEncodedSize(unEncodedSize);
        ByteBuffer encodedBases = ByteBuffer.allocate(encodedBasesSize);
        Iterator<Nucleotide> iterator = glyphs.iterator();
        List<Integer> gaps = encodeAll(iterator, unEncodedSize, encodedBases);
        encodedBases.flip();
        int numberOfBytesPerGap = computeBytesPerGapOffset(unEncodedSize);
        
        ByteBuffer result = ByteBuffer.allocate(HEADER_LENGTH + gaps.size()*numberOfBytesPerGap + encodedBasesSize);
        result.putInt(unEncodedSize);
       
        result.putInt(gaps.size());
        for(Integer gap : gaps){
            if(numberOfBytesPerGap==1){
                result.put(IOUtil.convertUnsignedByteToByteArray(gap.shortValue()));
            }else if(numberOfBytesPerGap==2){
                result.put(IOUtil.convertUnsignedShortToByteArray(gap.intValue()));
            }else{
                result.putInt(gap);
            }
        }
        result.put(encodedBases);
        return result.array();
    }
    /**
     * pack every 4 nucleotides into a single byte.
     * @param glyphs
     * @param unEncodedSize
     * @param result
     */
    private List<Integer> encodeAll(Iterator<Nucleotide> glyphs,
            final int unEncodedSize, ByteBuffer result) {
        List<Integer> gaps= new ArrayList<Integer>();
        for(int i=0; i<unEncodedSize; i+=NUCLEOTIDES_PER_BYTE){
            gaps.addAll(encodeNext4Values(glyphs, result,i));
        }
        return gaps;
    }
   
    private int computeHeaderlessEncodedSize(final int size) {
        return size/4 + (mod4(size)?0:1);
    }
    private boolean mod4(final int size) {
        return size%NUCLEOTIDES_PER_BYTE==0;
    }
    
    private byte getByteFor(Nucleotide nuc){
        switch(nuc){
            case Adenine : return (byte)0;
            case Cytosine : return (byte)1;
            case Guanine : return (byte)2;
            case Thymine : return (byte)3;
            default : throw new IllegalArgumentException("only A,C,G,T supported : "+ nuc);
        }
    }
    private Nucleotide getGlyphFor(byte b){
        if(b == (byte)0){
            return Nucleotide.Adenine;
        }
        if(b == (byte)1){
            return Nucleotide.Cytosine;
        }
        if(b == (byte)2){
            return Nucleotide.Guanine;
        }
        if(b == (byte)3){
            return Nucleotide.Thymine;
        }
        throw new IllegalArgumentException("unknown encoded value : "+b);
    }
   
    private List<Integer> encodeNext4Values(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
        byte b0 = glyphs.hasNext() ? getGappedByteFor(glyphs.next()) : 0;
        byte b1 = glyphs.hasNext() ? getGappedByteFor(glyphs.next()) : 0;
        byte b2 = glyphs.hasNext() ? getGappedByteFor(glyphs.next()) : 0;
        byte b3 = glyphs.hasNext() ? getGappedByteFor(glyphs.next()) : 0;
        
        List<Integer> gaps = new ArrayList<Integer>();
        if(b0== GAP_BYTE){
            gaps.add(offset);
            b0=0;
        }
        if(b1== GAP_BYTE){
            gaps.add(offset+1);
            b1=0;
        }
        if(b2== GAP_BYTE){
            gaps.add(offset+2);
            b2=0;
        }
        if(b3== GAP_BYTE){
            gaps.add(offset+3);
            b3=0;
        }
        result.put((byte) ((b0<<6 | b1<<4 | b2<<2 | b3) &0xFF));
        return gaps;
    }
    private byte getGappedByteFor(Nucleotide nucleotide){
        if(nucleotide == Nucleotide.Gap){
            return GAP_BYTE;
        }
        return getByteFor(nucleotide);
    }
    private List<Nucleotide> decodeNucleotidesIn(byte b) {
        byte b0 = (byte)((b &0xC0)>>>6);
        byte b1 = (byte)((b &0x30)>>>4);
        byte b2 = (byte)((b &0x0C)>>>2);
        byte b3 = (byte)(b &0x03);
       return Arrays.asList(getGlyphFor(b0),getGlyphFor(b1),getGlyphFor(b2),getGlyphFor(b3));
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
        int[] gaps =getGapOffsetsFrom(encodedGlyphs);
        List<Integer> ret = new ArrayList<Integer>(gaps.length);
        for(int i=0; i<gaps.length; i++){
            ret.add(Integer.valueOf(gaps[i]));
        }
        return ret;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps(byte[] encodedGlyphs) {
        return (int)IOUtil.readUnsignedInt(Arrays.copyOfRange(encodedGlyphs, 4, 8));
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
        int[] gaps =getGapOffsetsFrom(encodedGlyphs);
        return Arrays.binarySearch(gaps, gappedOffset)>=0;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getUngappedLength(byte[] encodedGlyphs) {
        int gappedLength= (int)IOUtil.readUnsignedInt(Arrays.copyOfRange(encodedGlyphs, 0, 4));
        
        return gappedLength - getNumberOfGaps(encodedGlyphs);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public List<Nucleotide> asUngappedList(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int length = decodedLengthOf(encodedGlyphs);
        int numberOfBytesPerGap = computeBytesPerGapOffset(length);
        int[] gaps = getGapOffsets(buf,numberOfBytesPerGap);
        List<Nucleotide> result = decodeNucleotidesWithGapSentientals(
                encodedGlyphs, length, numberOfBytesPerGap, gaps);
        for(int i= gaps.length-1; i>=0; i--){
            result.remove(i);
        }
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
        int[] gaps =getGapOffsetsFrom(encodedGlyphs);
        int i=Integer.MIN_VALUE;
        int numGaps=0;
        while(i<gappedOffset && numGaps<gaps.length){
            i =gaps[numGaps];
            numGaps++;
        }
        return numGaps;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
        int numGaps=getNumberOfGapsUntil(encodedGlyphs,gappedOffset);
        return gappedOffset-numGaps;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(byte[] encodedGlyphs, int ungappedOffset) {
        int[] gaps =getGapOffsetsFrom(encodedGlyphs);
        int currentOffset=ungappedOffset;
        for(int i=0; i<gaps.length && gaps[i]>currentOffset; i++){
            currentOffset++;
        }
        return currentOffset;
    }
    
}
