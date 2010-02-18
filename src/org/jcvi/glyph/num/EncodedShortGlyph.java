/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;

public class EncodedShortGlyph implements EncodedGlyphs<ShortGlyph>{

    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private final short[] data;
    public EncodedShortGlyph(List<ShortGlyph> shorts){
        this.data = encode(shorts);
    }
    private short[] encode(List<ShortGlyph> shorts) {
        ShortBuffer buffer = ShortBuffer.allocate(shorts.size());
        for(ShortGlyph byteGlyph : shorts){
            buffer.put(byteGlyph.getNumber().shortValue());
        }
        return buffer.array();
    }
    
    
    @Override
    public List<ShortGlyph> decode() {
         return FACTORY.getGlyphsFor(data);
    }

    @Override
    public ShortGlyph get(int index) {
        return FACTORY.getGlyphFor(data[index]);
    }

    @Override
    public long getLength() {
        return data.length;
    }
    @Override
    public List<ShortGlyph> decode(Range range) {
        List<ShortGlyph> result = new ArrayList<ShortGlyph>();
        for(long index : range){
            result.add(get((int)index));
        }
        return result;
    }

}
