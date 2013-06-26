/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
/**
 * {@code BasicNucleotideCodec} is a {@link NucleotideCodec}
 * that will store each base in 4 bits.
 * @author dkatzel
 *
 */
final class BasicNucleotideCodec extends AbstractNucleotideCodec{

	 public static final BasicNucleotideCodec INSTANCE = new BasicNucleotideCodec();
	    
	 private static final byte GAP_ORDINAL = Nucleotide.Gap.getOrdinalAsByte();
	    
	    private BasicNucleotideCodec(){
	        super(Nucleotide.Gap);
	    }
	    
	    @Override
		protected Nucleotide getNucleotide(byte encodedByte, int index){
	 	   byte value;
	    	if((index & 0x01)==0){
	 		   value = (byte)((encodedByte>>4) &0x0F);
	 	   }else{
	 		  value = (byte)(encodedByte &0x0F);
	 	   }
	 	   return getGlyphFor(value);
	    }
		@Override
		protected GrowableIntArray encodeNextGroup(Iterator<Nucleotide> glyphs, ByteBuffer result, int offset) {
	        byte b0 = glyphs.hasNext() ? getByteFor(glyphs.next()) : 0;
	        byte b1 = glyphs.hasNext() ? getByteFor(glyphs.next()) : 0;
	       
	        GrowableIntArray sentenielOffsets = new GrowableIntArray(4);
	        if(b0== GAP_ORDINAL){
	            sentenielOffsets.append(offset);
	            b0=0;
	        }
	        if(b1== GAP_ORDINAL){
	            sentenielOffsets.append(offset+1);
	            b1=0;
	        }
	        
	        result.put((byte) ((b0<<4 | b1) &0xFF));
	        return sentenielOffsets;
	    }
	    
	    @Override
		protected byte getByteFor(Nucleotide nuc) {
			return nuc.getOrdinalAsByte();
		}



		@Override
		protected Nucleotide getGlyphFor(byte b) {
			return Nucleotide.VALUES.get(b);
		}



		/**
	    * {@inheritDoc}
	    */
	    @Override
	    public List<Integer> getGapOffsets(byte[] encodedGlyphs) {
	    	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
			ValueSizeStrategy offsetStrategy = ValueSizeStrategy.values()[buf.get()];
	        //need to skip length since we don't care about it
			//but need to read it to advance pointer in buffer
			offsetStrategy.getNext(buf);
	        return getSentinelOffsetsFrom(buf, offsetStrategy);
	        
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
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
	    	return getGapOffsets(encodedGlyphs).contains(Integer.valueOf(gappedOffset));
	    }
	    
	    private ByteBuffer getBufferToComputeNumberOfGapsOnly(byte[] encodedBytes){
	    	//at most we only need the first 12 bytes
	    	//there is no need to wrap the entire array
	    	return ByteBuffer.wrap(encodedBytes,0, Math.min(encodedBytes.length, 12));
			
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
	        	return 0;
	        }
	        int numGaps= sentinelStrategy.getNext(buf);
	        return length-numGaps;
	    }
	   
	    /**
	    * {@inheritDoc}
	    */
	    @Override
	    public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
	    	int numGaps=0;
	    	Iterator<Integer> gapsIterator =getGapOffsets(encodedGlyphs).iterator();
	    	boolean done =false;
	    	while(!done && gapsIterator.hasNext()){
	    		int offset = gapsIterator.next();
	    		if(offset <=gappedOffset){
	    			numGaps++;
	    		}else{
	    			done=true;
	    		}
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
	    	int currentOffset=ungappedOffset;
	    	Iterator<Integer> gapsIterator =getGapOffsets(encodedGlyphs).iterator();
	    	boolean done =false;
	    	while(!done && gapsIterator.hasNext()){
	    		int gapOffset = gapsIterator.next();
	    		if(gapOffset <=ungappedOffset){
	    			currentOffset++;
	    		}else{
	    			done=true;
	    		}
	    	}
	    	return currentOffset;       
	    }
	@Override
	protected int getNucleotidesPerGroup() {
		return 2;
	}
}
