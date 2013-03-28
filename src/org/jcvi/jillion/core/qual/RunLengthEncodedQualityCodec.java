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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.jcvi.jillion.internal.core.util.RunLength;
/**
 * {@code RunLengthEncodedQualityCodec} is a {@link QualitySymbolCodec}
 * that encodes {@link PhredQuality} values in a run-length encoding.
 * Since reads often have clusters of basecalls with the same quality value
 * encoding them in a run-length format could have significant memory savings.
 * @author dkatzel
 *
 */
final class RunLengthEncodedQualityCodec implements QualitySymbolCodec{
	/**
	 * Singleton instance.
	 */
    public static final RunLengthEncodedQualityCodec INSTANCE = new RunLengthEncodedQualityCodec(Byte.MIN_VALUE);
    
    private final byte guard;

    RunLengthEncodedQualityCodec( byte guard){
        this.guard = guard;
    }
    
    
    public Iterator<PhredQuality> iterator(byte[] encodedData){
    	  ByteBuffer buf = ByteBuffer.wrap(encodedData);
          int size = buf.getInt();
          byte guard = buf.get();
          ValueSizeStrategy valueSizeStrategy = ValueSizeStrategy.values()[buf.get()];
          return new RunLengthIterator(buf, guard,valueSizeStrategy, size);
    }
    public Iterator<PhredQuality> iterator(byte[] encodedData, Range r){
  	  ByteBuffer buf = ByteBuffer.wrap(encodedData);
        int size = buf.getInt();
        if(r.getEnd()> size-1){
        	throw new IndexOutOfBoundsException(
        			String.format("can not iterate over %s when sequence is only %d long", r, size));
        }
        byte guard = buf.get();
        ValueSizeStrategy valueSizeStrategy = ValueSizeStrategy.values()[buf.get()];
        return new RunLengthIterator(buf, guard,valueSizeStrategy, r.getEnd()+1, r.getBegin());
  }
    
    private PhredQuality get(ByteBuffer buf, byte guard,  ValueSizeStrategy valueSizeStrategy, long index){
    	int currentOffset=0;
		while(buf.hasRemaining()){
            byte runLengthCode = buf.get(); 
            byte currentValue;
            if( runLengthCode == guard){                                  
            	int count = valueSizeStrategy.getNext(buf);            	 
            	if(count==0){
            		currentOffset++;
            		currentValue = guard;
            	}else{
            		currentValue = buf.get();  
            		currentOffset+=count;
            	}
            }
            else{
            	currentOffset++;
            	currentValue = runLengthCode;
            }
            if(currentOffset>index){
            	return PhredQuality.valueOf(currentValue);
            }
    	}
		//should not happen, any method that calls this
		//should have done bounds checking but this
		//is required to get it to compile.
		throw new IndexOutOfBoundsException("could not find index "+index);
    }

   
    @Override
    public PhredQuality decode(byte[] encodedGlyphs, long index) {
    	 if(index <0){
         	throw new IndexOutOfBoundsException("can not have negative length");
         }
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int length=buf.getInt();       
        if(index >=length){
        	throw new IndexOutOfBoundsException("can not have index beyond length");
        }
        byte guard = buf.get();
        ValueSizeStrategy valueSizeStrategy = ValueSizeStrategy.values()[buf.get()];
        return get(buf,guard,valueSizeStrategy, index);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        return buf.getInt();
    }
    
    @Override
    public byte[] encode(Collection<PhredQuality> glyphs) {
        List<RunLength<PhredQuality>> runLengthList = runLengthEncode(glyphs);
        return createEncodedByteArray(glyphs.size(), runLengthList);
    }
    
    public byte[] encode(byte[] qualities){
    	 List<RunLength<PhredQuality>> runLengthList = runLengthEncode(qualities);
         return createEncodedByteArray(qualities.length, runLengthList);
    }
    public byte[] encode(Iterable<PhredQuality> qualityIterable, int numberOfQualities) {
        List<RunLength<PhredQuality>> runLengthList = runLengthEncode(qualityIterable);
        return createEncodedByteArray(numberOfQualities, runLengthList);
    }
	private byte[] createEncodedByteArray(int numberOfQualities,
			List<RunLength<PhredQuality>> runLengthList) {
		Metrics metrics = new Metrics(runLengthList);
		ValueSizeStrategy sizeStrategy = metrics.getSizeStrategy();
        ByteBuffer buf = ByteBuffer.allocate(metrics.computeEncodingSize());
        buf.putInt(numberOfQualities);
        buf.put(guard);
        buf.put((byte)sizeStrategy.ordinal());
        for(RunLength<PhredQuality> runLength : runLengthList){
            if(runLength.getValue().getQualityScore() == guard){
                
                for(int repeatCount = 0; repeatCount<runLength.getLength(); repeatCount++){
                    buf.put(guard);
                    sizeStrategy.put(buf, 0);
                }
               
            }
            else{
                if(runLength.getLength() ==1){
                    buf.put(runLength.getValue().getQualityScore());
                }
                else{
                    buf.put(guard);
                    sizeStrategy.put(buf, runLength.getLength());
                    buf.put(runLength.getValue().getQualityScore());
                }
            }
        }
        return buf.array();
	}

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + guard;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof RunLengthEncodedQualityCodec)){
            return false;
        }
        RunLengthEncodedQualityCodec other = (RunLengthEncodedQualityCodec) obj;
        if (guard != other.guard){
            return false;
        }
        return true;
    }
    private static List<RunLength<PhredQuality>> runLengthEncode(byte[] qualities){
    	int currentOffset=0;

    	if(qualities.length==0){
    		return Collections.emptyList();
    	}
    	List<RunLength<PhredQuality>> encoding = new ArrayList<RunLength<PhredQuality>>();
    	byte currentElement=qualities[currentOffset];
    	int runLength=1;
    	currentOffset++;
    	while(currentOffset< qualities.length){
    		byte nextElement = qualities[currentOffset];
    		if(currentElement ==nextElement){
    			runLength++;
    		}else{
    			encoding.add(new RunLength<PhredQuality>(PhredQuality.valueOf(currentElement), runLength));
    			runLength=1;
    			currentElement=nextElement;
    		}
    		currentOffset++;
    	}
    	encoding.add(new RunLength<PhredQuality>(PhredQuality.valueOf(currentElement), runLength));
    	
    	return encoding;
    }
    
    private static <T> List<RunLength<T>> runLengthEncode(Iterable<T> elements){
    	Iterator<T> iter = elements.iterator();
    	if(!iter.hasNext()){
    		return Collections.emptyList();
    	}
    	List<RunLength<T>> encoding = new ArrayList<RunLength<T>>();
    	T currentElement=iter.next();
    	int runLength=1;
    	while(iter.hasNext()){
    		T nextElement = iter.next();
    		if(currentElement.equals(nextElement)){
    			runLength++;
    		}else{
    			encoding.add(new RunLength<T>(currentElement, runLength));
    			runLength=1;
    			currentElement=nextElement;
    		}
    	}
    	encoding.add(new RunLength<T>(currentElement, runLength));
    	
    	return encoding;
    }
   
   
    private static final class RunLengthIterator implements Iterator<PhredQuality>{
		private long currentOffset;
		private final ByteBuffer buf;
		private final byte guard;
		private final long length;
		private PhredQuality currentQuality;
		private int currentRunEndOffset;
		private final ValueSizeStrategy valueSizeStrategy;
		
		RunLengthIterator(ByteBuffer buf, byte guard,ValueSizeStrategy valueSizeStrategy, long length){
			this(buf,guard,valueSizeStrategy, length,0L);
		}
		RunLengthIterator(ByteBuffer buf, byte guard, ValueSizeStrategy valueSizeStrategy,long length, long startOffset){
			this.buf = buf;
			this.guard =guard;
			this.valueSizeStrategy = valueSizeStrategy;
			currentOffset=startOffset;
			this.length = length;
			populateCurrentRun();
			while(currentOffset>=currentRunEndOffset){
				populateCurrentRun();
			}
		}
		@Override
		public boolean hasNext() {
			return currentOffset<length;
		}


		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}

		@Override
		public PhredQuality next() {
			if(!hasNext()){
				throw new NoSuchElementException("offset = "+currentOffset);
			}
			if(currentOffset>=currentRunEndOffset){
				populateCurrentRun();
			}
			currentOffset++;
			return currentQuality;
		}
		
		private void populateCurrentRun(){
			byte runLengthCode = buf.get(); 
            byte currentValue;
            if( runLengthCode == guard){                                  
            	int count = valueSizeStrategy.getNext(buf);          	 
            	if(count==0){
            		currentRunEndOffset++;
            		currentValue = guard;
            	}else{
            		currentValue = buf.get();  
            		currentRunEndOffset+=count;
            	}
            }
            else{
            	currentRunEndOffset++;
            	currentValue = runLengthCode;
            }
            currentQuality = PhredQuality.valueOf(currentValue);
		}
	}
    
    private class Metrics{
    	private int numGuards=0;
    	private int singletons=0;
    	private int nonSingletons=0;
        
    	private int maxRunLength=0;
        
    	private final ValueSizeStrategy sizeStrategy;
    	public Metrics(List<RunLength<PhredQuality>> runLengthList){
    		for(RunLength<PhredQuality> runLength : runLengthList){
    			int length = runLength.getLength();
    			if(length > maxRunLength){
    				maxRunLength = length;
    			}
                if(runLength.getValue().getQualityScore() == guard){
                    numGuards+=length;
                }
                else if(length ==1){
                    singletons++;
                }
                else{
                    nonSingletons++;
                }
                
            }
    		
    		sizeStrategy = ValueSizeStrategy.getStrategyFor(maxRunLength);
    	}
    	
    	public int computeEncodingSize() {
    		//each qual value is 1 byte 
            
            int bytesPerLength = sizeStrategy.getNumberOfBytesPerValue();
            //header is 4 bytes for length + 1 byte for guard + 1 byte for size strategy
			int header = 6;
			//each guarded entry is sizeStrategy + qual value
			int sizeOfGuardedSections = numGuards *(bytesPerLength +1);
			//each singleton is a qual value
			int sizeOfSingletons = singletons;
			//each non-singleton is sizeStrategy + qual value + guard
			int sizeOfNonSingletons = nonSingletons * (bytesPerLength + 2);
			return header+sizeOfGuardedSections+ sizeOfSingletons+sizeOfNonSingletons;
        }

		public final ValueSizeStrategy getSizeStrategy() {
			return sizeStrategy;
		}
    	
    	
    }
}
