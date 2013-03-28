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
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.internal.core.GlyphCodec;
import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;

/**
 * {@code TwoBitEncodedNucleotideCodec} is a {@link GlyphCodec}
 * of Nucletotides that can encode a list of {@link Nucleotide}s
 * that only contain A,C,G,T and gaps (no ambiguities) in as little as 2 bits per base
 * plus some extra bytes for storing the gaps. This should 
 * greatly reduce the memory footprint of most kinds of read data.
 * @author dkatzel
 */
final class NoAmbiguitiesEncodedNucleotideCodec extends TwoBitEncodedNucleotideCodec{
    public static final NoAmbiguitiesEncodedNucleotideCodec INSTANCE = new NoAmbiguitiesEncodedNucleotideCodec();
    
    static boolean canEncode(Iterable<Nucleotide> nucleotides){
        for(Nucleotide n :nucleotides){
            if(n.isAmbiguity()){
                return false;
            }
        }
        return true;
    }
    private NoAmbiguitiesEncodedNucleotideCodec(){
        super(Nucleotide.Gap);
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
    	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
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
    /**
    * {@inheritDoc}
    */
    @Override
    public long getUngappedLength(byte[] encodedGlyphs) {
    	ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
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
    
}
