/*
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

public class DefaultByteGlyphCodec extends AbstractByteGlyphCodec<ByteGlyph>{

    private final ByteGlyphFactory<ByteGlyph> FACTORY;
   
    public DefaultByteGlyphCodec(ByteGlyphFactory<ByteGlyph> factory){
        this.FACTORY = factory;
    }

    @Override
    protected ByteGlyph getValueOf(byte b) {
        return FACTORY.getGlyphFor(b);
    }

    


}
