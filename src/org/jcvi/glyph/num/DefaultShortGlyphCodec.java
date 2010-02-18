/*
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;

public final class DefaultShortGlyphCodec implements GlyphCodec<ShortGlyph>{

    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    
    private static final DefaultShortGlyphCodec INSTANCE = new DefaultShortGlyphCodec();
    private DefaultShortGlyphCodec(){
        
    }
    public static DefaultShortGlyphCodec getInstance(){
        return INSTANCE;
    }
    @Override
    public List<ShortGlyph> decode(byte[] encodedGlyphs) {
        List<ShortGlyph> glyphs = new ArrayList<ShortGlyph>();
        ShortBuffer buf = ByteBuffer.wrap(encodedGlyphs).asShortBuffer();
        while(buf.hasRemaining()){
            glyphs.add(FACTORY.getGlyphFor(buf.get()));
        }
        return glyphs;
    }

    @Override
    public ShortGlyph decode(byte[] encodedGlyphs, int index) {
        int indexIntoShortAray = index*2;
        final int hi = encodedGlyphs[indexIntoShortAray]<<8;
        final byte low = encodedGlyphs[indexIntoShortAray+1];
        int value = hi | (low & 0xFF);
        return FACTORY.getGlyphFor((short)value);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length/2;
    }

    @Override
    public byte[] encode(List<ShortGlyph> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size()*2);
        for(ShortGlyph g : glyphs){
            buf.putShort(g.getNumber());
        }
        return buf.array();
    }

}
