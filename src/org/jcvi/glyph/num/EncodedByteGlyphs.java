/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;

public class EncodedByteGlyphs implements EncodedGlyphs<ByteGlyph>{

    private static final ByteGlyphFactory<ByteGlyph> FACTORY = new ByteGlyphFactory<ByteGlyph>(){

        @Override
        protected ByteGlyph createNewGlyph(Byte b) {
            return new ByteGlyph(b);
        }
        
    };
    private final byte[] data;
    public EncodedByteGlyphs(List<ByteGlyph> bytes){
        this.data = encode(bytes);
    }
    private byte[] encode(List<ByteGlyph> bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(bytes.size());
        for(ByteGlyph byteGlyph : bytes){
            buffer.put(byteGlyph.getNumber().byteValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ByteGlyph> decode() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ByteGlyph get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ByteGlyph> decode(Range range) {
        List<ByteGlyph> result = new ArrayList<ByteGlyph>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

}
