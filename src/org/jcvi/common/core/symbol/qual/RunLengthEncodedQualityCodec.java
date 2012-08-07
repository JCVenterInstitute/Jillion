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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.qual;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.util.RunLength;
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
    @Override
    public List<PhredQuality> decode(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int size = buf.getInt();
        byte guard = buf.get();
       
        return decode(decodeUpTo(buf, guard, size-1));
    }
    
    private PhredQuality get(ByteBuffer buf, byte guard,  int index){
    	int currentOffset=0;
		while(buf.hasRemaining()){
            byte runLengthCode = buf.get(); 
            byte currentValue;
            if( runLengthCode == guard){                                  
            	int count = buf.getShort();            	 
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
		throw new IllegalStateException("could not find index "+index);
    }
    private List<RunLength<PhredQuality>> decodeUpTo(ByteBuffer buf, byte guard,int maxIndex) {
        //size of list initialized to 25% of max index
        //since we expect the run length to be pretty well compressed
        //tests on 454 sequences show rle takes up only 16% memory footprint
        List<RunLength<PhredQuality>> runLengthList = new ArrayList<RunLength<PhredQuality>>(maxIndex/4);
        int lengthDecoded=0;
        while(lengthDecoded<=maxIndex && buf.hasRemaining()){
            byte value = buf.get();            
            if( value == guard){                                  
            	lengthDecoded += handleRun(buf, guard, runLengthList);
            }
            else{
            	//if not guard, just output token
                lengthDecoded += handleSingleToken(value, runLengthList);
            }
        }
        return runLengthList;
    }
    private int handleRun(ByteBuffer buf, byte guard,
            List<RunLength<PhredQuality>> runLengthList) {
        int count = buf.getShort();
        if(count ==0){
        	//count is 0 so guard byte must be actual value.
            return handleSingleToken(guard, runLengthList);
        }
        byte repValue = buf.get();                   
        runLengthList.add(new RunLength<PhredQuality>(PhredQuality.valueOf(repValue),count));
        return count;
          
    }
    private int handleSingleToken(byte guard,
            List<RunLength<PhredQuality>> runLengthList) {
        runLengthList.add(new RunLength<PhredQuality>(PhredQuality.valueOf(guard),1));
        return 1;
    }

    @Override
    public PhredQuality decode(byte[] encodedGlyphs, int index) {
    	 if(index <0){
         	throw new IllegalArgumentException("can not have negative length");
         }
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int length=buf.getInt();       
        if(index >=length){
        	throw new IllegalArgumentException("can not have index beyond length");
        }
        byte guard = buf.get();
        return get(buf,guard,index);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        return buf.getInt();
    }
    
    @Override
    public byte[] encode(Collection<PhredQuality> glyphs) {
        List<RunLength<PhredQuality>> runLengthList = runLengthEncode(glyphs);
        int size = computeSize(runLengthList);
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.putInt(glyphs.size());
        buf.put(guard);
        for(RunLength<PhredQuality> runLength : runLengthList){
            if(runLength.getValue().getQualityScore() == guard){
                
                for(int repeatCount = 0; repeatCount<runLength.getLength(); repeatCount++){
                    buf.put(guard);
                    buf.putShort((byte)0);
                }
               
            }
            else{
                if(runLength.getLength() ==1){
                    buf.put(runLength.getValue().getQualityScore());
                }
                else{
                    buf.put(guard);
                    buf.putShort((short)runLength.getLength());
                    buf.put(runLength.getValue().getQualityScore());
                }
            }
        }
        return buf.array();
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
		int size = computeSize(runLengthList);
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.putInt(numberOfQualities);
        buf.put(guard);
        for(RunLength<PhredQuality> runLength : runLengthList){
            if(runLength.getValue().getQualityScore() == guard){
                
                for(int repeatCount = 0; repeatCount<runLength.getLength(); repeatCount++){
                    buf.put(guard);
                    buf.putShort((byte)0);
                }
               
            }
            else{
                if(runLength.getLength() ==1){
                    buf.put(runLength.getValue().getQualityScore());
                }
                else{
                    buf.put(guard);
                    buf.putShort((short)runLength.getLength());
                    buf.put(runLength.getValue().getQualityScore());
                }
            }
        }
        return buf.array();
	}

    private int computeSize(List<RunLength<PhredQuality>> runLengthList) {
        int numGuards=0;
        int singletons=0;
        int nonSingletons=0;
        for(RunLength<PhredQuality> runLength : runLengthList){
            if(runLength.getValue().getQualityScore() == guard){
                numGuards+=runLength.getLength();
            }
            else if(runLength.getLength() ==1){
                singletons++;
            }
            else{
                nonSingletons++;
            }
            
        }
        
        return 4+1+(numGuards *3)+ singletons+(nonSingletons *4);
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
   
    
    private static <T> List<T> decode(List<RunLength<T>> encoding){
        List<T> decoded = new ArrayList<T>();
        for(RunLength<T> runLength : encoding){
            final T value = runLength.getValue();
            for(int i=0; i< runLength.getLength(); i++){                
                decoded.add(value);
            }
        }
        return decoded;
    }

}
