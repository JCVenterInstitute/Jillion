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
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;


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
	private static final int END_OF_ITER = Integer.MIN_VALUE;
        private static final ValueSizeStrategy[] VALUE_SIZE_STRATEGIES = ValueSizeStrategy.values();
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
        /**
         * This is a sentinel value for a gap.  Since we 
         * can only store 2 bits per base, a byte of 5 is too big.
         * 
         */
        private static final byte GAP_BYTE = 5;
        /**
         * This is the 5th {@link Nucleotide} of our 2 bit encoding
         * usually a "-" or "N".  These 5th bases will occasionally occur
         * but infrequently enough that we should still use our
         * 2 bit encoding for all sequences.
         */
        private final Nucleotide sententialBase;
        protected TwoBitEncodedNucleotideCodec(Nucleotide sententialBase){
            this.sententialBase = sententialBase;
        }
        
       private Nucleotide getNucleotide2(byte encodedByte, int index){
    	   //endian is backwards
    	   int j = (3-index%4)*2;
    	   return getGlyphFor((byte)((encodedByte>>j) &0x3));
       }
       
		protected List<Integer> getSentinelOffsetsFrom(ByteBuffer buf, ValueSizeStrategy offsetStrategy){
			ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
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
        
		
        
       
        @Override
        public Nucleotide decode(byte[] encodedGlyphs, long index){
        	if(index <0){
        		throw new IndexOutOfBoundsException(String.format("offset %d can not be negative ", index));
        	}
        	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
            ValueSizeStrategy offsetStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
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

            int offsetIntoBitSet = (int)(index%4);
            return getNucleotide2(buf.get(), offsetIntoBitSet);
        
        }
        private boolean isSentinelOffset(ByteBuffer buf, ValueSizeStrategy offsetStrategy, int index) {
        	ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
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
            return VALUE_SIZE_STRATEGIES[buf.get()].getNext(buf);
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
			private final int[] sentinelArray;
			
			private int nextSentinel;
			private int currentOffset=0;
			private int sentinelIndex=0;
			private final byte[] encodedBytes;
			
			public IteratorImpl(byte[] encodedGlyphs){
				ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
				ValueSizeStrategy offsetStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
	            this.length =offsetStrategy.getNext(buf);
	            this.sentinelArray = parseSentinelOffsetsIteratorFrom(buf,offsetStrategy);
	            this.encodedBytes =new byte[buf.remaining()];
	            buf.get(encodedBytes);
	            this.nextSentinel = getNextSentinel();	           
			}
			
			public IteratorImpl(byte[] encodedGlyphs, Range range){
				ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
				ValueSizeStrategy offsetStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
	            int sequenceLength =offsetStrategy.getNext(buf);
	            if(range.getBegin()<0 || range.getEnd()>=sequenceLength){
					throw new IndexOutOfBoundsException("range "+range +" is out of range of sequence which is only "+ new Range.Builder(sequenceLength).build());
				}
	            this.length = (int)range.getEnd()+1;
	            this.sentinelArray = parseSentinelOffsetsIteratorFrom(buf,offsetStrategy);
	            this.nextSentinel = getNextSentinel();
	            currentOffset = (int)range.getBegin();
	            while(nextSentinel!=END_OF_ITER && nextSentinel < currentOffset){
	            	this.nextSentinel = getNextSentinel();
	            }
	            this.encodedBytes =new byte[buf.remaining()];
	            buf.get(encodedBytes);
	           	           
			}
			
			private int[] parseSentinelOffsetsIteratorFrom(
					ByteBuffer buf, ValueSizeStrategy offsetStrategy) {
				ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
	            //no sentinels (no gaps or N's)
	            if(sentinelStrategy == ValueSizeStrategy.NONE){
	            	return new int[0];
	            }else{            	
	            	int numberOfSentinels = sentinelStrategy.getNext(buf);
	            	//
	            	int[] sentinelArray = new int[numberOfSentinels];
	            	
	            	for(int i = 0; i< numberOfSentinels; i++){
	            		sentinelArray[i] =offsetStrategy.getNext(buf);
	            	}
	            	return sentinelArray;
	            }
			}
			
			private int getNextSentinel() {
				if(sentinelIndex>= sentinelArray.length){
					return END_OF_ITER;
				}
				return sentinelArray[sentinelIndex++];
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
				if(nextSentinel !=END_OF_ITER && nextSentinel == currentOffset){
            		nextSentinel = getNextSentinel();
            		currentOffset++;
            		return sententialBase;
				}
				Nucleotide next= getNucleotide2(encodedBytes[currentOffset/4], currentOffset%4);
				currentOffset++;
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("can not modify immutable sequence");
				
				
			}
			
		}
        
}
