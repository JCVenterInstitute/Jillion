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
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;


public abstract class AbstractDeltaEncoderCodec<G extends NumericSymbol> implements GlyphCodec<G>{

    private final DeltaEncoder deltaEncoder;
    private final ValueSizeStrategy valueSizeStrategy;
    
    public AbstractDeltaEncoderCodec(DeltaEncoder deltaEncoder,ValueSizeStrategy valueSizeStrategy){
        this.deltaEncoder = deltaEncoder;
        this.valueSizeStrategy = valueSizeStrategy;
    }

	private ByteBuffer convertToByteBuffer(Collection<G> glyphs) {
		ByteBuffer buf = ByteBuffer.allocate(glyphs.size() * valueSizeStrategy.numberOfBytesPerValue());
		for(G glyph : glyphs){
			valueSizeStrategy.put(glyph.getValue().longValue(), buf);
		}
		buf.flip();
		return buf;
	}
	@Override
    public List<G> decode(byte[] encodedGlyphs) {
        ByteBuffer buffer = ByteBuffer.wrap(encodedGlyphs);
        ByteBuffer decompressedBuffer = ByteBuffer.allocate(encodedGlyphs.length);
        
        long lastValue=0, secondToLastValue=0, thirdToLastValue=0;
        
        while(buffer.hasRemaining()){
            long value =valueSizeStrategy.getNext(buffer) + deltaEncoder.computeDelta(lastValue, secondToLastValue, thirdToLastValue);
            valueSizeStrategy.put(value, decompressedBuffer);
            thirdToLastValue = secondToLastValue;
            secondToLastValue = lastValue;
            lastValue = value;
        }
        decompressedBuffer.flip();
        return convertToGlyphs(decompressedBuffer);
    }

    protected abstract List<G> convertToGlyphs(ByteBuffer decodedData);   
    @Override
    public G decode(byte[] encodedGlyphs, int index) {
        // TODO make more efficient
        return decode(encodedGlyphs).get(index);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
    	return encodedGlyphs.length /valueSizeStrategy.numberOfBytesPerValue();
        
    }

    @Override
    public byte[] encode(Collection<G> glyphs) {
        int length = glyphs.size() *valueSizeStrategy.numberOfBytesPerValue();
        ByteBuffer result = ByteBuffer.allocate(length);
        ByteBuffer glyphsAsBytes = convertToByteBuffer(glyphs);
        long delta=0;
        long prevValue=0;
        long prevPrevValue=0;
        long prevPrevPrevValue =0;
        
        //special case for 1st glyph
        if(glyphsAsBytes.hasRemaining()){
        	 prevValue =  valueSizeStrategy.getNext(glyphsAsBytes);
             valueSizeStrategy.put(prevValue,result );
        }
        while(glyphsAsBytes.hasRemaining()){     
        	delta = deltaEncoder.computeDelta(prevValue, prevPrevValue, prevPrevPrevValue);
             
          
            prevPrevPrevValue= prevPrevValue;
            prevPrevValue= prevValue;
            prevValue =  valueSizeStrategy.getNext(glyphsAsBytes);
            valueSizeStrategy.put(prevValue-delta,result );
        }
        
        return result.array();
    }

}
