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

/**
 * @author dkatzel
 *
 *
 */
abstract class TwoBitEncodedNucleotideCodec implements NucleotideCodec{
        /*
         * Implementation Details:
         * ====================================
         * We store everything as a single byte array which
         * contains a header with the decoded size, and number of gaps as ints.
         * Header
         * int32: decoded size
         * int32: #gaps
         * 
         * Next, we store gaps offsets (if any)
         * We can use the decoded size to figure out how many
         * bits per offset we need (unsigned). Anything <256 (like a next-gen read)
         * only needs 1 byte while sanger/ small contig consensuses can fit in 2 bytes.
         * 
         * Finally, the rest of the byte array contains the ACGT- basecalls
         * stored as 2bits each.  A gap is recorded here to keep offsets correct.
         * 
         * We can find a basecall by pulling out the gap offsets and seeing if 
         * the offset we want is there.  If so return gap, else compute offset into encoded 
         * byte array for ACGT call and then do bit shifting to get the 2bits we need.
         */
       
       
        
        /**
         * The header will contain 2 int values specifying how many nucleotides
         * total are encoded plus how many gaps.
         */
        private static final int HEADER_LENGTH = 8;
        
        /**
         * We can store ACGTs as 2 bits so that's 4 per byte.
         */
        private static final int NUCLEOTIDES_PER_BYTE =4;

        private static final int UNSIGNED_BYTE_MAX = 255;
        
        private static final int UNSIGNED_SHORT_MAX = 65531;
        /**
         * This is a sentinel value for a gap.  Since we 
         * can only store 2 bits per base, a byte of 5 is too big.
         * 
         */
        private static final byte GAP_BYTE = 5;
        
        private final Nucleotide sententialBase;
        protected TwoBitEncodedNucleotideCodec(Nucleotide sententialBase){
            this.sententialBase = sententialBase;
        }
        /**
         * We can compress our data more if the length
         * is small enough that any possible
         * gap index will fit in only 1/2 or 4 bytes.
         * @param length the length of the nucleotide sequence
         * to encode.
         * @return 1 2 or 4 depending on how many
         * bytes are required to store each offset for the length.
         */
        protected final int computeBytesPerSentinelOffset(int length){
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
            int numberOfBytesPerSentinel = computeBytesPerSentinelOffset(length);
            int[] gaps = getSentinelOffsets(buf,numberOfBytesPerSentinel);
            List<Nucleotide> result = decodeNucleotidesWithSentinels(
                    encodedGlyphs, length, numberOfBytesPerSentinel, gaps);
            for(int i=0; i<gaps.length; i++){
                int gapOffset = gaps[i];
                //we had to put something in the gap
                //location as a place holder so get rid of it
                result.remove(gapOffset);
                result.add(gapOffset, sententialBase);
            }
            
            return result;
        }
        protected final List<Nucleotide> decodeNucleotidesWithSentinels(
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
        protected final int[] getSentinelOffsets(ByteBuffer buf, int bytesPerOffset){
            buf.position(4);
            int[] sentinels = new int[buf.getInt()];
            
            for(int i=0; i<sentinels.length; i++){
                switch(bytesPerOffset){
                    case 1 : sentinels[i] =IOUtil.convertToUnsignedByte(buf.get());
                            break;
                    case 2 : sentinels[i] =IOUtil.convertToUnsignedShort(buf.getShort());
                            break; 
                    default : sentinels[i] =buf.getInt();
                            break;
                }
            }
            return sentinels;
        }
        
        protected final  int[] getSentienelOffsetsFrom(byte[] encodedData){
            int length = decodedLengthOf(encodedData);
            int numberOfBytesPerGap = computeBytesPerSentinelOffset(length);
           
            int[] gaps = getSentinelOffsets(ByteBuffer.wrap(encodedData),numberOfBytesPerGap);
          
            return gaps;
        }
        @Override
        public Nucleotide decode(byte[] encodedGlyphs, int index){
            int length = decodedLengthOf(encodedGlyphs);
            int numberOfBytesPerGap = computeBytesPerSentinelOffset(length);
           
            int[] sentinels = getSentinelOffsets(ByteBuffer.wrap(encodedGlyphs),numberOfBytesPerGap);
            if(Arrays.binarySearch(sentinels, index)>=0){
                return sententialBase;
            }
            final byte getByteForGlyph = getEncodedByteForGlyph(encodedGlyphs,sentinels.length,numberOfBytesPerGap,index);
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
        private int computeEncodedIndexForGlyph(int numberOfSentinels,int numberOfBytesPerSentinel,int index) {
            if(index<0){
                throw new IllegalArgumentException("index can not be negative: "+index);
            }
            final int encodedIndexForGlyph = HEADER_LENGTH+numberOfBytesPerSentinel*numberOfSentinels+index/4;
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
        @Override
        public byte[] encode(Nucleotide glyph) {
            return encodeNucleotides(Arrays.asList(glyph),1);
            
        }
        private byte[] encodeNucleotides(Collection<Nucleotide> glyphs,
                final int unEncodedSize) {
            int encodedBasesSize = computeHeaderlessEncodedSize(unEncodedSize);
            ByteBuffer encodedBases = ByteBuffer.allocate(encodedBasesSize);
            Iterator<Nucleotide> iterator = glyphs.iterator();
            List<Integer> sentinels = encodeAll(iterator, unEncodedSize, encodedBases);
            encodedBases.flip();
            int numberOfBytesPerGap = computeBytesPerSentinelOffset(unEncodedSize);
            
            ByteBuffer result = ByteBuffer.allocate(HEADER_LENGTH + sentinels.size()*numberOfBytesPerGap + encodedBasesSize);
            result.putInt(unEncodedSize);
           
            result.putInt(sentinels.size());
            for(Integer sentinel : sentinels){
                if(numberOfBytesPerGap==1){
                    result.put(IOUtil.convertUnsignedByteToByteArray(sentinel.shortValue()));
                }else if(numberOfBytesPerGap==2){
                    result.put(IOUtil.convertUnsignedShortToByteArray(sentinel.intValue()));
                }else{
                    result.putInt(sentinel);
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
            byte b0 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
            byte b1 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
            byte b2 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
            byte b3 = glyphs.hasNext() ? getSentienelByteFor(glyphs.next()) : 0;
            
            List<Integer> sentenielOffsets = new ArrayList<Integer>();
            if(b0== GAP_BYTE){
                sentenielOffsets.add(offset);
                b0=0;
            }
            if(b1== GAP_BYTE){
                sentenielOffsets.add(offset+1);
                b1=0;
            }
            if(b2== GAP_BYTE){
                sentenielOffsets.add(offset+2);
                b2=0;
            }
            if(b3== GAP_BYTE){
                sentenielOffsets.add(offset+3);
                b3=0;
            }
            result.put((byte) ((b0<<6 | b1<<4 | b2<<2 | b3) &0xFF));
            return sentenielOffsets;
        }
        
        
        private byte getSentienelByteFor(Nucleotide nucleotide){
            if(nucleotide == sententialBase){
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
        
}
