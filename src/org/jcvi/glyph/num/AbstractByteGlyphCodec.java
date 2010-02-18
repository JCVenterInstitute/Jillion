/*
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;

public abstract class AbstractByteGlyphCodec<G extends ByteGlyph> implements GlyphCodec<G>{

    protected abstract G getValueOf(byte b);
    @Override
    public List<G> decode(byte[] encodedGlyphs) {
        List<G> glyphs = new ArrayList<G>();
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        while(buf.hasRemaining()){
            glyphs.add(getValueOf(buf.get()));
        }
        return glyphs;
    }

    @Override
    public G decode(byte[] encodedGlyphs, int index) {
        return getValueOf(encodedGlyphs[index]);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length;
    }

    @Override
    public byte[] encode(List<G> glyphs) {
        ByteBuffer buf = ByteBuffer.allocate(glyphs.size());
        for(ByteGlyph g : glyphs){
            buf.put(g.getNumber().byteValue());
        }
        return buf.array();
    }
}
