/*
 * Created on Sep 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.charset.Charset;
import java.util.List;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;

public class TigrPeaksEncoderGlyphCodec implements GlyphCodec<ShortGlyph>{

    private static final ShortGlyphFactory GLYPH_FACTORY = ShortGlyphFactory.getInstance();
    private static final Charset CHARSET = Charset.forName("US-ASCII");
    
    public static final TigrPeaksEncoderGlyphCodec instance = new TigrPeaksEncoderGlyphCodec();
    
    private TigrPeaksEncoderGlyphCodec(){}
    
    
    public static TigrPeaksEncoderGlyphCodec getInstance() {
        return instance;
    }


    @Override
    public List<ShortGlyph> decode(byte[] encodedGlyphs) {
        String encodedString = new String(encodedGlyphs);
        return GLYPH_FACTORY.getGlyphsFor(
                TigrPeaksEncoder.decode(encodedString));
    }

    @Override
    public ShortGlyph decode(byte[] encodedGlyphs, int index) {
        return GLYPH_FACTORY.getGlyphFor(
                TigrPeaksEncoder.decode(new String(encodedGlyphs,CHARSET), index+1)[index]);
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return decode(encodedGlyphs).size();
    }

    @Override
    public byte[] encode(List<ShortGlyph> glyphs) {
        return TigrPeaksEncoder.encode(ShortGlyph.toArray(glyphs)).getBytes(CHARSET);
    }

   
}
