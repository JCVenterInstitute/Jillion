/*
 * Created on Jul 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.num.NumericGlyph;

public abstract class AbstractDeltaEncoderCodec<N extends NumericGlyph> implements GlyphCodec<N>{

    private final DeltaEncoder deltaEncoder;
    private final ValueSizeStrategy valueSizeStrategy;
    
    public AbstractDeltaEncoderCodec(DeltaEncoder deltaEncoder,ValueSizeStrategy valueSizeStrategy){
        this.deltaEncoder = deltaEncoder;
        this.valueSizeStrategy = valueSizeStrategy;
    }
    
    @Override
    public List<N> decode(byte[] encodedGlyphs) {
        ByteBuffer buffer = ByteBuffer.wrap(encodedGlyphs);
        int uncompressedSize = buffer.getInt();
        ByteBuffer decompressedBuffer = ByteBuffer.allocate(uncompressedSize);
        
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

    protected abstract List<N> convertToGlyphs(ByteBuffer decodedData);
    protected abstract ByteBuffer convertToByteBuffer(List<N> glyphs);    
    protected abstract int numberOfBytesPerGlyph();
    @Override
    public N decode(byte[] encodedGlyphs, int index) {
        // TODO make more efficient
        return decode(encodedGlyphs).get(index);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        ByteBuffer buffer = ByteBuffer.wrap(encodedGlyphs);
        return buffer.getInt();
    }

    @Override
    public byte[] encode(List<N> glyphs) {
        int size = glyphs.size() *numberOfBytesPerGlyph();
        ByteBuffer result = ByteBuffer.allocate(4+ valueSizeStrategy.numberOfBytesPerValue() *size);
        result.putInt(size);
        ByteBuffer glyphsAsBytes = convertToByteBuffer(glyphs);
        long delta=0;
        long prevValue=0;
        long prevPrevValue=0;
        long prevPrevPrevValue =0;
        while(glyphsAsBytes.hasRemaining()){           
            delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
            prevPrevPrevValue= prevPrevValue;
            prevPrevValue= prevValue;
            prevValue =  valueSizeStrategy.getNext(glyphsAsBytes);
            valueSizeStrategy.put(prevValue -delta,result );
        }
        
        return result.array();
    }

}
