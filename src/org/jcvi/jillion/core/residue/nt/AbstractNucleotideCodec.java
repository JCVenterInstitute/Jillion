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
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.core.util.iter.SingleElementIterator;
import org.jcvi.jillion.core.util.streams.BiIntConsumer;
import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.jcvi.jillion.internal.core.residue.nt.DefaultLeftFlankingNoGapIterator;
import org.jcvi.jillion.internal.core.residue.nt.DefaultRightFlankingNoGapIterator;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;


/**
 * Encodes
 * nucleotides as in as few bits as possible
 * plus some extra bytes to store offsets
 * for a special "sentinel" base (usually gaps).  
 * 
 * 
 * This allows quick random access of bases
 * even computing from gapped to ungapped values.
 * 
 * 
 * @author dkatzel
 *
 *
 */
abstract class AbstractNucleotideCodec implements NucleotideCodec{
	

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
         * This is the 5th {@link Nucleotide} of our 2 bit encoding
         * usually a "-" or "N".  These 5th bases will occasionally occur
         * but infrequently enough that we should still use our
         * 2 bit encoding for all sequences.
         */
        private final Nucleotide sententialBase;
        protected AbstractNucleotideCodec(Nucleotide sententialBase){
            this.sententialBase = sententialBase;
        }
        
        protected abstract int getNucleotidesPerGroup();
        
        
       protected abstract Nucleotide getNucleotide(byte encodedByte, int index);
       
       private ByteBuffer getBufferToComputeNumberOfGapsOnly(byte[] encodedBytes){
	    	//at most we only need the first 12 bytes
	    	//there is no need to wrap the entire array
	    	return ByteBuffer.wrap(encodedBytes,0, Math.min(encodedBytes.length, 12));
			
	    }
       /**
	    * {@inheritDoc}
	    */
	    @Override
	    public int getUngappedOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
	        int numGaps=getNumberOfGapsUntil(encodedGlyphs,gappedOffset);
//	        return Math.min(gappedOffset, (int) getLength(encodedGlyphs) -1) -numGaps;
	        return gappedOffset-numGaps;
	    }
	    
	    
	    private static boolean _encodingHasGaps(byte[] encodedGlyphs) {
    		ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        //need to skip length since we don't care about it
			//but need to read it to advance pointer in buffer
			offsetStrategy.getNext(buf);
	        ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
            return sentinelStrategy != ValueSizeStrategy.NONE;
    }

		/**
	    * {@inheritDoc}
	    */
	    @Override
	    public int getGappedOffsetFor(byte[] encodedGlyphs, int ungappedOffset) {
	    	int currentOffset=ungappedOffset;
	    	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        //need to skip length since we don't care about it
			//but need to read it to advance pointer in buffer
			offsetStrategy.getNext(buf);
	        ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
            if(sentinelStrategy == ValueSizeStrategy.NONE){
            	//no gaps
            	return currentOffset;
            }
            int numberOfSentinels = sentinelStrategy.getNext(buf);
            
            for(int i = 0; i< numberOfSentinels; i++, currentOffset++){
            	int currentGapOffset =offsetStrategy.getNext(buf);
            	if(currentGapOffset >currentOffset){
                	return currentOffset;
                }
            }
            return currentOffset;
	    }
       /**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
	    	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        //need to skip length since we don't care about it
			//but need to read it to advance pointer in buffer
			offsetStrategy.getNext(buf);
	        ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
            if(sentinelStrategy == ValueSizeStrategy.NONE){
            	//no gaps
            	return false;
            }
            int numberOfSentinels = sentinelStrategy.getNext(buf);
            
            int firstGapOffset =offsetStrategy.getNext(buf);            
            
            //special case that first gap is after the offset we care about
            if(firstGapOffset >gappedOffset){
            	return false;
            }
            //actually need to check the gap offset
            //since we don't want to skip it!
            if(firstGapOffset == gappedOffset){
            	return true;
            }
            int currentGapOffset = firstGapOffset;
            for(int i = 1; i< numberOfSentinels; i++){
            	currentGapOffset =offsetStrategy.getNext(buf);
            	if(currentGapOffset == gappedOffset){
            		//found it
            		return true;
            	}
            	if(currentGapOffset >gappedOffset){
            		//past it
            		return false;
            	}
            }
            //we checked all the gap offsets
            //and didn't find a match
            //so it must be not a gap
            return false;
	    }
       /**
	    * {@inheritDoc}
	    */
	    @Override
	    public long getUngappedLength(byte[] encodedGlyphs) {
	    	ByteBuffer buf = getBufferToComputeNumberOfGapsOnly(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        int length =offsetStrategy.getNext(buf);
	        ValueSizeStrategy sentinelStrategy = ValueSizeStrategy.values()[buf.get()];
	        if(sentinelStrategy == ValueSizeStrategy.NONE){
	        	//no gaps
	        	return length;
	        }
	        int numGaps= sentinelStrategy.getNext(buf);
	        return length-numGaps;
	    }

	@Override
	public long getLength(byte[] encodedData) {
		ByteBuffer buf = getBufferToComputeNumberOfGapsOnly(encodedData);
		ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
		return offsetStrategy.getNext(buf);
	}

	/**
	    * {@inheritDoc}
	    */
	    @Override
	    public int getNumberOfGaps(byte[] encodedGlyphs) {
	    	ByteBuffer buf = getBufferToComputeNumberOfGapsOnly(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        //need to read the next few bytes even though we
			//don't care what the size is
			offsetStrategy.getNext(buf);
	        ValueSizeStrategy sentinelStrategy = ValueSizeStrategy.values()[buf.get()];
	        if(sentinelStrategy == ValueSizeStrategy.NONE){
	        	return 0;
	        }
	        return sentinelStrategy.getNext(buf);
	    }
        
	    private static class NonGapsRightIterator implements OfInt{
	    	private int nextNonGap;
	    	private int lastNonGapOffset;
	    	private final GapIterator gapIterator;
	    	private Integer nextGap;
	    	
	    	public NonGapsRightIterator(int startOffset, GapIterator gapIterator, int lastNonGapOffset) {
	    		this.gapIterator = gapIterator;
	    		iterateUntilCorrectGapPosition(startOffset);
	    		this.lastNonGapOffset = lastNonGapOffset;
	    		nextNonGap=startOffset;
//	    		update();
	    	}
	    	
	    	@Override
			public boolean hasNext() {
				return nextNonGap<=lastNonGapOffset;
			}

			@Override
			public int nextInt() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				if(nextGap==null || nextNonGap < nextGap) {
					
					return nextNonGap++;
				}
				//we are past the next gap need to update
				
				nextNonGap++;
				update();
				return nextNonGap++;
			}

			private void update() {
				nextGap=null;
				
	    		while(gapIterator.hasNext()) {
	    			int ng = gapIterator.nextInt();
	    			if(ng==nextNonGap) {
	    				nextNonGap++;
	    			}else {
	    				nextGap=ng;
	    				break;
	    			}
	    		}
			}

			private void iterateUntilCorrectGapPosition(int startOffset){
	    		nextGap=null;
	    		while(gapIterator.hasNext()) {
	    			int ng = gapIterator.nextInt();
	    			if(ng>=startOffset) {
	    				nextGap=ng;
	    				break;
	    			}
	    		}
	    	}
	    }
	    
	    private static class NonGapsLeftIterator implements OfInt{

	    	private int nextNonGap;
	    	private final GrowableIntArray gaps;
	    	
	    	public NonGapsLeftIterator(int gappedOffset, GrowableIntArray gaps) {
	    		
	    		this.gaps = gaps;
	    		computeNextNonGap(gappedOffset);
	    	}
	    	private void computeNextNonGap(int start) {
	    		nextNonGap=start;
	    		while(nextNonGap>=0) {
	    			if(gaps.binarySearch(nextNonGap) <0) {
	    				//not a gap
	    				break;
	    			}
	    			nextNonGap--;
	    		}
	    	}
	    	
			@Override
			public boolean hasNext() {
				return nextNonGap>=0;
			}

			@Override
			public int nextInt() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				int ret = nextNonGap;
				computeNextNonGap(nextNonGap-1);
				return ret;
			}
	    	
	    }
	    
	 @Override
	 public OfInt createLeftFlankingNonGapIterator(byte[] encodedGlyphs, int startingGapOffset) {
		GapIterator iter = GapIterator.create(encodedGlyphs);
		if(!iter.hasNext()) {
			return new DefaultLeftFlankingNoGapIterator(startingGapOffset);
		}
		//iterate to the farthest gap position and then walk back through all of our gap offsets
		GrowableIntArray gaps = new GrowableIntArray();
		
		iter.stopWhen(StopCondition.spy(StopCondition.GoUntilGapOffset(startingGapOffset, true),
				(gapOffset, ignored) ->{
					gaps.append(gapOffset);
				}
				));
		if(gaps.getCurrentLength() ==0) {
			//no gaps yet
			return new DefaultLeftFlankingNoGapIterator(startingGapOffset);
		}
		return new NonGapsLeftIterator(startingGapOffset, gaps);
	 }
	 @Override
	 public OfInt createRightFlankingNonGapIterator(byte[] encodedGlyphs, int startingGapOffset) {
		GapIterator iter = GapIterator.create(encodedGlyphs);
		if(!iter.hasNext()) {
			return new DefaultRightFlankingNoGapIterator(startingGapOffset, (int) getLength(encodedGlyphs) -1);
		}
		return new NonGapsRightIterator(startingGapOffset, iter,getLeftFlankingNonGapOffsetFor(encodedGlyphs, (int) getLength(encodedGlyphs) -1));
	 }
	  

		@Override
		public int getLeftFlankingNonGapOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
	    	GapIterator iter = GapIterator.create(encodedGlyphs);
			if(!iter.hasNext()) {
				//no gaps
				return gappedOffset;
			}
			return getLeftFlankingNonGapOffset(gappedOffset, iter);
		}
	    
	    

		private int getLeftFlankingNonGapOffset(int gappedOffset, GapIterator iter) {
			GrowableIntArray gaps = new GrowableIntArray();
			
			iter.stopWhen(StopCondition.spy(StopCondition.GoUntilGapOffset(gappedOffset, true),
					(gapOffset, ignored) ->{
						gaps.append(gapOffset);
					}
					));
			int current = gappedOffset;
			int lengthWalked=0;
			while(current >0 && lengthWalked < gaps.getCurrentLength()) {
				
				if(gaps.binarySearch(current) >=0) {
					current--;
					lengthWalked++;
				}else {
					break;
				}
			}
			return current;
		}

		@Override
		public Range getExpandingFlankingNonGapRangeFor(byte[] encodedGlyphs,Rangeable gappedRange) {
			if(!_encodingHasGaps(encodedGlyphs)) {
				//no gaps
				return gappedRange.asRange();
			}
			
			return Range.of( getLeftFlankingNonGapOffsetFor(encodedGlyphs, (int) gappedRange.getBegin()),
					getRightFlankingNonGapOffsetFor(encodedGlyphs, (int) gappedRange.getEnd()));
		}
		
		@Override
		public Range getExpandingFlankingNonGapRangeFor(byte[] encodedGlyphs,int gappedBegin, int gappedEnd) {
			if(!_encodingHasGaps(encodedGlyphs)) {
				//no gaps
				return Range.of(gappedBegin, gappedEnd);
			}
			
			return Range.of( getLeftFlankingNonGapOffsetFor(encodedGlyphs, gappedBegin),
							getRightFlankingNonGapOffsetFor(encodedGlyphs, gappedEnd));
		}

		@Override
		public Range getContractingFlankingNonGapRangeFor(byte[] encodedGlyphs,int gappedBegin, int gappedEnd) {
			if(!_encodingHasGaps(encodedGlyphs)) {
				//no gaps
				return Range.of(gappedBegin, gappedEnd);
			}
			
			return Range.of(  getRightFlankingNonGapOffsetFor(encodedGlyphs, gappedBegin),
					getLeftFlankingNonGapOffsetFor(encodedGlyphs, gappedEnd));
					
		}
		@Override
		public Range getContractingFlankingNonGapRangeFor(byte[] encodedGlyphs,Rangeable gappedRange) {
			if(!_encodingHasGaps(encodedGlyphs)) {
				//no gaps
				return gappedRange.asRange();
			}
			//we can't delegate because we lose the intermediate stack
			//there are edge cases where the left side iterates to a gap we need to consider for the right side
			
			
			return Range.of(  getRightFlankingNonGapOffsetFor(encodedGlyphs, (int)gappedRange.getBegin()),
					getLeftFlankingNonGapOffsetFor(encodedGlyphs, (int)gappedRange.getEnd()));
					
		}

		@Override
		public int getRightFlankingNonGapOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
			GapIterator iter = GapIterator.create(encodedGlyphs);
			if(!iter.hasNext()) {
				//no gaps
				return gappedOffset;
			}
			return getRightFlankingNonGapOffset(gappedOffset, iter);
			
		}
		private int getRightFlankingNonGapOffset(int gappedOffset, GapIterator iter) {
			
			SingleThreadAdder numFlankingGaps = new SingleThreadAdder();
			
			iter.stopWhen(StopCondition.GoUntilGapOffset(gappedOffset, false));
			if(iter.currentGapOffset <=gappedOffset) {
				if(iter.currentGapOffset == gappedOffset) {
					numFlankingGaps.increment();				
				}
				iter.stopWhen(StopCondition.spy(StopCondition.GoToNextNonGap(iter.currentGapOffset),
												(ignore1, ignore2)-> 	numFlankingGaps.increment()));
			}
			return gappedOffset + numFlankingGaps.intValue();
		}
		
		
		
		
	    protected GrowableIntArray getSentinelOffsets(byte[] encodedGlyphs){
	    	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        //need to skip length since we don't care about it
			//but need to read it to advance pointer in buffer
			offsetStrategy.getNext(buf);
			ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
            if(sentinelStrategy == ValueSizeStrategy.NONE){
            	return new GrowableIntArray();
        	}else{            	
            	//there are sentinels
            	int numberOfSentinels = sentinelStrategy.getNext(buf);
            	GrowableIntArray sentinelOffsets = new GrowableIntArray(numberOfSentinels);
            	for(int i = 0; i< numberOfSentinels; i++){
            		sentinelOffsets.append(offsetStrategy.getNext(buf));            	
            	}
            	return sentinelOffsets;
            }
	    }
	    
	    
	   
	   
	    private static class GapIterator implements OfInt{

	    	private static final GapIterator EMPTY = new GapIterator(0, null,null);
	    	static {
	    		EMPTY.i = 0;
	    	}
	    	private int numberOfSentinels;
	    	private ValueSizeStrategy strategy;
	    	private ByteBuffer buf;
	    	private int currentGapOffset;
	    	
	    	private int i=0;
	    	
	    	private GapIterator(int numberOfSentinels, ValueSizeStrategy strategy, ByteBuffer buf) {
				this.numberOfSentinels = numberOfSentinels;
				this.strategy = strategy;
				this.buf = buf;
			}
	    	

			public static GapIterator create(byte[] encodedGlyphs) {
	    		ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
				ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
		        //need to skip length since we don't care about it
				//but need to read it to advance pointer in buffer
				offsetStrategy.getNext(buf);
		        ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
	            if(sentinelStrategy == ValueSizeStrategy.NONE){
	            	//no gaps
	            	return EMPTY;
	            }
	            int numberOfSentinels = sentinelStrategy.getNext(buf);
	            return new GapIterator(numberOfSentinels, offsetStrategy, buf);
	    	}
	    	
			@Override
			public boolean hasNext() {
				return strategy!=null && i< numberOfSentinels;
			}

			@Override
			public int nextInt() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				
				currentGapOffset =  strategy.getNext(buf);
				i++;
				
				return currentGapOffset;
			}
			
			public int getNumberOfGapsSoFar() {
				return i;
			}
			
			public void stopWhen(StopCondition condition) {
				ConditionResponse[] response = new ConditionResponse[1];
				if(!hasNext()) {
					return;
				}
				for(; i< numberOfSentinels; ) {
					boolean halt=false;
					consumeNext((a,b) -> {
						response[0] = condition.shouldStop(a, b);
					});
					switch(response[0]) {
					case HALT_AND_DECREMENT:
						i--;
					case HALT:
						halt=true;
						break;
					default:
						break;
					}
					if(halt) {
						break;
					}
					
				}
				
				
			}
			public void consumeNext(BiIntConsumer gapAndIConsumer) {
				int ithGap = i;
				gapAndIConsumer.accept(nextInt(), ithGap);
			}
	    	
	    }
	    private enum ConditionResponse{
	    	HALT,
	    	INCREMENT,
	    	HALT_AND_DECREMENT
	    	;
	    }
	    private static interface StopCondition {
	    	
	    	
	    	ConditionResponse shouldStop(int gapOffset, int ithGap);
	    	
	    	
	    	public static StopCondition spy(StopCondition condition, BiIntConsumer spy) {
	    		return (a,b)->{
	    			ConditionResponse resp = condition.shouldStop(a, b);
	    			if(resp ==ConditionResponse.INCREMENT) {
	    				spy.accept(a, b);
	    			}
	    			return resp;
	    		};
	    	}
	    	
	    	public static StopCondition GoUntilGapOffset(int gappedOffset, boolean inclusive) {
	    		if(inclusive) {
	    		return (a,b)-> {
	    			
	    			if(a > gappedOffset) {
	    				return ConditionResponse.HALT_AND_DECREMENT;
	    			}
	    			return ConditionResponse.INCREMENT;
	    		};
	    		}else {
	    			return (a,b)-> {
		    			if(a > gappedOffset) {
		    				return ConditionResponse.HALT_AND_DECREMENT;
		    			}
		    			if(a == gappedOffset) {
		    				return ConditionResponse.HALT;
		    			}
		    			return ConditionResponse.INCREMENT;
		    		};
	    		}
	    	}
	    	
	    	public static StopCondition GoToNextNonGap(int startOffset) {
	    		int[] prevOffset = new int[1];
	    		prevOffset[0]=startOffset;
	    		
	    		return (a,b)->{
	    			if(a -1 > prevOffset[0]) {
	    				return ConditionResponse.HALT;
	    			}
	    			prevOffset[0] = a;
	    			return ConditionResponse.INCREMENT;
	    		};
	    	}
		}
		@Override
		public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
			
			GapIterator iter = GapIterator.create(encodedGlyphs);
			iter.stopWhen(StopCondition.GoUntilGapOffset(gappedOffset, true));
			return iter.getNumberOfGapsSoFar();
			
		
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
            int bytesToSkip = (int)(index/getNucleotidesPerGroup());
            buf.position(currentPosition+ bytesToSkip);

            int indexIntoByte = (int)(index%getNucleotidesPerGroup());
            return getNucleotide(buf.get(), indexIntoByte);
        
        }
        private boolean isSentinelOffset(ByteBuffer buf, ValueSizeStrategy offsetStrategy, int index) {
        	ValueSizeStrategy sentinelStrategy = VALUE_SIZE_STRATEGIES[buf.get()];
        	if(sentinelStrategy == ValueSizeStrategy.NONE){
        		return false;
        	}
        	int numberOfSentinels = sentinelStrategy.getNext(buf);
        	int nextSentinelOffset= Integer.MIN_VALUE;
        	//offsets are sorted so if we get to
        	//the desired index we can short circuit the for loop.
        	//we can't do a binarysearch very easily
        	//because the offsets are packed many into 
        	//one byte so we would have to read them all
        	//in O(n) anyway.
        	//it is also important that if we 
        	//don't find the sentinel value
        	//that the buffer has advanced to the end of the
        	//section so we can start getting 
        	//the other basecalls.
        	for(int i = 0; i< numberOfSentinels; i++){
        		nextSentinelOffset = offsetStrategy.getNext(buf);
				if(index ==nextSentinelOffset){
        			return true;
        		}
        	}
        	
			return false;
		}
		
       

        @Override
		public byte[] encode(int numberOfNucleotides, int[] gapOffsets,
				Iterator<Nucleotide> nucleotides) {
        	 return encodeNucleotides(nucleotides, gapOffsets, numberOfNucleotides);
		}
		
        /**
         * Convenience method to encode a single basecall.
         * @param nt the Nucleotide to encode; can not be null.
         * @return the encoded nucleotide
         * 
         * @throws NullPointerException if nt is null.
         */
        @Override
        public byte[] encode(Nucleotide nt) {
        	final int gapOffsets[];
        	if(nt.isGap()){
        		gapOffsets = new int[]{0};
        	}else{
        		gapOffsets = new int[0];
        	}
            return encodeNucleotides(new SingleElementIterator<>(nt), gapOffsets, 1);
            
        }
        
        public int getNumberOfEncodedBytesFor(int totalLength, int numberOfSentinelValues){
        	int encodedBasesSize = computeHeaderlessEncodedSize(totalLength);
        	ValueSizeStrategy numBasesSizeStrategy = ValueSizeStrategy.getStrategyFor(totalLength);
            ValueSizeStrategy sentinelSizeStrategy = numberOfSentinelValues==0
            											?	ValueSizeStrategy.NONE 
            											:	ValueSizeStrategy.getStrategyFor(numberOfSentinelValues);
            return computeEncodedBufferSize(encodedBasesSize,
					numBasesSizeStrategy, numberOfSentinelValues,
					sentinelSizeStrategy);
        }
        
        private byte[] encodeNucleotides(Iterator<Nucleotide> iterator, int[] sentienelOffsetArray,
                final int unEncodedSize) {
            int encodedBasesSize = computeHeaderlessEncodedSize(unEncodedSize);
            ByteBuffer encodedBases = ByteBuffer.allocate(encodedBasesSize);
            encodeAll(iterator, unEncodedSize, encodedBases);
            encodedBases.flip();
            ValueSizeStrategy numBasesSizeStrategy = ValueSizeStrategy.getStrategyFor(unEncodedSize);
            int numberOfSentinels = sentienelOffsetArray.length;
			ValueSizeStrategy sentinelSizeStrategy = numberOfSentinels==0
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
            	for(int i=0; i<numberOfSentinels; i++){
            		numBasesSizeStrategy.put(result, sentienelOffsetArray[i]);
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
        private void encodeAll(Iterator<Nucleotide> glyphs,
                final int unEncodedSize, ByteBuffer result) {
        	
            int groupSize = getNucleotidesPerGroup();
            //create variable i outside of for loop
            //so it can be used afterwards
            int i=0;
			for(; i<unEncodedSize-groupSize; i+=groupSize){
				encodeCompleteGroup(glyphs, result,i);
            }
			//need this check incase we are empty
			if(i<unEncodedSize){
				encodeLastGroup(glyphs, result,i);
			}
        }
       
        protected int computeHeaderlessEncodedSize(final int size) {
            return (size+3)/getNucleotidesPerGroup();
        }
       
        
        protected abstract byte getByteFor(Nucleotide nuc);
        
        protected abstract Nucleotide getGlyphFor(byte b);
       
        protected abstract void encodeCompleteGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset);
        
        protected abstract void encodeLastGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset);
     
        protected byte getSentienelByteFor(Nucleotide nucleotide){
            if(nucleotide.equals(sententialBase)){
                return 0;
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
			IteratorImpl iter = (IteratorImpl)iterator(encodedData);
			StringBuilder builder = new StringBuilder(iter.getLength());
			while(iter.hasNext()){
				builder.append(iter.next());
			}
			return builder.toString();
		}

		private final class IteratorImpl implements Iterator<Nucleotide>{
			
			private final int length;
			private final int[] sentinelArray;
			private final int numberOfBasesPerGroup = getNucleotidesPerGroup();
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
			
			public int getLength() {
				return length;
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
				if(nextSentinel == currentOffset){
            		nextSentinel = getNextSentinel();
            		currentOffset++;
            		return sententialBase;
				}
				int arrayoffset = currentOffset/numberOfBasesPerGroup;
				int groupIndex = currentOffset%numberOfBasesPerGroup;
				Nucleotide next= getNucleotide(encodedBytes[arrayoffset], groupIndex);
				currentOffset++;
				return next;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("can not modify immutable sequence");
				
				
			}
			
		}
		
		
		
        
}
