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

package org.jcvi.jillion.core.residue.nt;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.internal.io.ValueSizeStrategy;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;


/**
 * {@code TwoBitEncodedNucleotideCodec} is a
 * {@link NucleotideCodec} that
 * is able to encode
 * up to 5 different nucleotides as 2 bits each
 * plus some extra bytes to store offsets
 * of the 5th base.  The best uses of this
 * codec is to store ACGT and -/N as the 5th base type.
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
         * byte : ordinal of ValueSizeStrategy
         * 1 -4 bytes: decoded size packed according to valueSizeStrategy
         * byte : ordinal of ValueSizeStrategy for number of gaps
         * 0 -4 bytes: decoded #gaps packed according to valueSizeStrategy.  
         * if previous ordinal of ValueStrategy was for ValueSizeStrategy#NONE
         * then this field is 0 bytes long.
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
       
		protected final NucleotideSequenceBuilder populateNucleotideSequenceBuilderFrom(
				byte[] encodedGlyphs) {
			ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
            int length =offsetStrategy.getNext(buf);
            final Iterator<Integer> sentinelIterator = parseSentinelOffsetsIteratorFrom(buf,offsetStrategy);
            NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(length);
            BitSet bits =IOUtil.toBitSet(buf);
            Integer nextSentinel = getNextSentinel(sentinelIterator);
            for(int i=0; i<length; i++){
            	if(nextSentinel !=null && nextSentinel.intValue() == i){
            		builder.append(sententialBase);
            		nextSentinel = getNextSentinel(sentinelIterator);
            	}else{
            		final Nucleotide nextBase = getNucletotide(bits, i);
            		builder.append(nextBase);
            	}
            }
			return builder;
		}
		private Nucleotide getNucletotide(BitSet bits, int i) {
			final Nucleotide nextBase;
			//endian is backwards
			int j = (3-i%4)*2;
			int offsetOfByte = (i/4)*8 ;
			BitSet bitset = bits.get(offsetOfByte+j, offsetOfByte+j+2);
			if(bitset.isEmpty()){
				nextBase =getGlyphFor((byte)0);            			
			}else{
				byte[] byteArray = IOUtil.toByteArray(bitset,2);
				
				byte byteValue = new BigInteger(byteArray).byteValue();
				nextBase = getGlyphFor(byteValue);
			}
			return nextBase;
		}
		private Iterator<Integer> parseSentinelOffsetsIteratorFrom(
				ByteBuffer buf, ValueSizeStrategy offsetStrategy) {
			ValueSizeStrategy sentinelStrategy = ValueSizeStrategy.values()[buf.get()];
            final Iterator<Integer> sentinelIterator;
            if(sentinelStrategy == ValueSizeStrategy.NONE){
            	sentinelIterator=IteratorUtil.createEmptyIterator();
            }else{            	
            	//there are gaps
            	int numberOfSentinels = sentinelStrategy.getNext(buf);
            	List<Integer> sentinelOffsets = new ArrayList<Integer>(numberOfSentinels);
            	for(int i = 0; i< numberOfSentinels; i++){
            		sentinelOffsets.add(Integer.valueOf(offsetStrategy.getNext(buf)));
            	}
            	sentinelIterator = sentinelOffsets.iterator();
            }
			return sentinelIterator;
		}
		
		protected List<Integer> getSentinelOffsetsFrom(ByteBuffer buf, ValueSizeStrategy offsetStrategy){
			ValueSizeStrategy sentinelStrategy = ValueSizeStrategy.values()[buf.get()];
            if(sentinelStrategy == ValueSizeStrategy.NONE){
            	return Collections.<Integer>emptyList();
            }else{            	
            	//there are gaps
            	int numberOfSentinels = sentinelStrategy.getNext(buf);
            	List<Integer> sentinelOffsets = new ArrayList<Integer>(numberOfSentinels);
            	for(int i = 0; i< numberOfSentinels; i++){
            		sentinelOffsets.add(Integer.valueOf(offsetStrategy.getNext(buf)));
            	}
            	return sentinelOffsets;
            }

		}
        private Integer getNextSentinel(Iterator<Integer> sentinelIterator) {
			if(sentinelIterator.hasNext()){
				return sentinelIterator.next();
			}
			return null;
		}
		
        protected final int[] getSentinelOffsets(ByteBuffer buf, int bytesPerOffset){
            buf.position(4);
            int[] sentinels = new int[buf.getInt()];
            
            for(int i=0; i<sentinels.length; i++){
                switch(bytesPerOffset){
                    case 1 : sentinels[i] =IOUtil.toUnsignedByte(buf.get());
                            break;
                    case 2 : sentinels[i] =IOUtil.toUnsignedShort(buf.getShort());
                            break; 
                    default : sentinels[i] =buf.getInt();
                            break;
                }
            }
            return sentinels;
        }
        
       
        @Override
        public Nucleotide decode(byte[] encodedGlyphs, long index){
        	if(index <0){
        		throw new IndexOutOfBoundsException(String.format("offset %d can not be negative ", index));
        	}
        	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
            ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
            int length=offsetStrategy.getNext(buf);
            if(index >=length){
            	throw new IndexOutOfBoundsException(String.format("offset %d is >= length (%d)", index,length));
            }
            if(isSentinelOffset(buf,offsetStrategy,(int)index)){
            	return sententialBase;
            }
            int currentPosition =buf.position();
            int bytesToSkip = (int)(index/4);
            buf.position(currentPosition+ bytesToSkip);
            
            BitSet bits =IOUtil.toBitSet(buf.get());
            int offsetIntoBitSet = (int)(index%4);
            return getNucletotide(bits, offsetIntoBitSet);
        
        }
        private boolean isSentinelOffset(ByteBuffer buf, ValueSizeStrategy offsetStrategy, int index) {
        	ValueSizeStrategy sentinelStrategy = ValueSizeStrategy.values()[buf.get()];
        	if(sentinelStrategy == ValueSizeStrategy.NONE){
        		return false;
        	}
        	int numberOfSentinels = sentinelStrategy.getNext(buf);
        	int nextSentinelOffset= Integer.MIN_VALUE;
        	//even though the offsets are sorted so if we get past
        	//the desired index we can short circuit the for loop
        	//we don't want to do that because 
        	//we need to read thru the entire sentinel
        	//section of the buffer
        	for(int i = 0; i< numberOfSentinels; i++){
        		nextSentinelOffset = offsetStrategy.getNext(buf);
				if(index ==nextSentinelOffset){
        			return true;
        		}
        	}
        	
			return false;
		}
		
       

        @Override
		public byte[] encode(int numberOfNucleotides,
				Iterator<Nucleotide> nucleotides) {
        	 return encodeNucleotides(nucleotides, numberOfNucleotides);
		}
		@Override
        public byte[] encode(Collection<Nucleotide> glyphs) {
            final int unEncodedSize = glyphs.size();
            return encodeNucleotides(glyphs.iterator(), unEncodedSize);
            
        }
        /**
         * Convenience method to encode a single basecall.
         * @param glyph
         * @return
         */
        @Override
        public byte[] encode(Nucleotide glyph) {
            return encodeNucleotides(Arrays.asList(glyph).iterator(),1);
            
        }
        
        public static int getNumberOfEncodedBytesFor(int totalLength, int numberOfSentinelValues){
        	int encodedBasesSize = computeHeaderlessEncodedSize(totalLength);
        	ValueSizeStrategy numBasesSizeStrategy = ValueSizeStrategy.getStrategyFor(totalLength);
            ValueSizeStrategy sentinelSizeStrategy = numberOfSentinelValues==0
            											?	ValueSizeStrategy.NONE 
            											:	ValueSizeStrategy.getStrategyFor(numberOfSentinelValues);
            return computeEncodedBufferSize(encodedBasesSize,
					numBasesSizeStrategy, numberOfSentinelValues,
					sentinelSizeStrategy);
        }
        private byte[] encodeNucleotides(Iterator<Nucleotide> iterator,
                final int unEncodedSize) {
            int encodedBasesSize = computeHeaderlessEncodedSize(unEncodedSize);
            ByteBuffer encodedBases = ByteBuffer.allocate(encodedBasesSize);
            List<Integer> sentinels = encodeAll(iterator, unEncodedSize, encodedBases);
            encodedBases.flip();
            ValueSizeStrategy numBasesSizeStrategy = ValueSizeStrategy.getStrategyFor(unEncodedSize);
            int numberOfSentinels = sentinels.size();
			ValueSizeStrategy sentinelSizeStrategy = sentinels.isEmpty()
            											?	ValueSizeStrategy.NONE 
            											:	ValueSizeStrategy.getStrategyFor(numberOfSentinels);
            
            int bufferSize = computeEncodedBufferSize(encodedBasesSize,
					numBasesSizeStrategy, numberOfSentinels,
					sentinelSizeStrategy);
            
            ByteBuffer result = ByteBuffer.allocate(bufferSize);
            result.put((byte)numBasesSizeStrategy.ordinal());
            numBasesSizeStrategy.put(result, unEncodedSize);
            result.put((byte)sentinelSizeStrategy.ordinal());
            if(sentinelSizeStrategy != ValueSizeStrategy.NONE){
            	sentinelSizeStrategy.put(result, numberOfSentinels);
            	for(Integer sentinel : sentinels){
            		numBasesSizeStrategy.put(result, sentinel.intValue());
                }
            }
            result.put(encodedBases);
            return result.array();
        }
		private static int computeEncodedBufferSize(int encodedBasesSize,
				ValueSizeStrategy numBasesSizeStrategy, int numberOfSentinels,
				ValueSizeStrategy sentinelSizeStrategy) {
			int bufferSize = 2 + numBasesSizeStrategy.getNumberOfBytesPerValue() + sentinelSizeStrategy.getNumberOfBytesPerValue()
            		+ numBasesSizeStrategy.getNumberOfBytesPerValue() * numberOfSentinels + encodedBasesSize;
			return bufferSize;
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
       
        private static int computeHeaderlessEncodedSize(final int size) {
            return (size+3)/4;
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
     
       
        @Override
        public int decodedLengthOf(byte[] encodedGlyphs) {
            ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
            return ValueSizeStrategy.values()[buf.get()].getNext(buf);
        }
		@Override
		public Iterator<Nucleotide> iterator(byte[] encodedData) {
			return new IteratorImpl(encodedData);
		}
		
		@Override
		public Iterator<Nucleotide> iterator(byte[] encodedData, Range range) {
			return new IteratorImpl(encodedData, range);
		}
		@Override
		public String toString(byte[] encodedData) {
			Iterator<Nucleotide> iter = iterator(encodedData);
			StringBuilder builder = new StringBuilder(decodedLengthOf(encodedData));
			while(iter.hasNext()){
				builder.append(iter.next());
			}
			return builder.toString();
		}

		private final class IteratorImpl implements Iterator<Nucleotide>{
			private final int length;
			private final Iterator<Integer> sentinelIterator;
			private final BitSet bits;
			private Integer nextSentinel;
			private int currentOffset=0;
			
			public IteratorImpl(byte[] encodedGlyphs){
				ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
				ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	            this.length =offsetStrategy.getNext(buf);
	            this.sentinelIterator = parseSentinelOffsetsIteratorFrom(buf,offsetStrategy);
	            this.bits =IOUtil.toBitSet(buf);
	            this.nextSentinel = getNextSentinel(sentinelIterator);	           
			}
			
			public IteratorImpl(byte[] encodedGlyphs, Range range){
				ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
				ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	            int sequenceLength =offsetStrategy.getNext(buf);
	            if(range.getBegin()<0 || range.getEnd()>=sequenceLength){
					throw new IndexOutOfBoundsException("range "+range +" is out of range of sequence which is only "+ new Range.Builder(sequenceLength).build());
				}
	            this.length = (int)range.getEnd()+1;
	            this.sentinelIterator = parseSentinelOffsetsIteratorFrom(buf,offsetStrategy);
	            this.nextSentinel = getNextSentinel(sentinelIterator);
	            currentOffset = (int)range.getBegin();
	            while(nextSentinel!=null && nextSentinel.intValue() < currentOffset){
	            	this.nextSentinel = getNextSentinel(sentinelIterator);
	            }
	            this.bits =IOUtil.toBitSet(buf);
	           	           
			}
			@Override
			public boolean hasNext() {
				return currentOffset<length;
			}

			@Override
			public Nucleotide next() {
				if(!hasNext()){
					throw new NoSuchElementException("no more elements");
				}
				if(nextSentinel !=null && nextSentinel.intValue() == currentOffset){
            		nextSentinel = getNextSentinel(sentinelIterator);
            		currentOffset++;
            		return sententialBase;
				}
				Nucleotide next= getNucletotide(bits, currentOffset);
				currentOffset++;
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("can not modify immutable sequence");
				
				
			}
			
		}
        
}
