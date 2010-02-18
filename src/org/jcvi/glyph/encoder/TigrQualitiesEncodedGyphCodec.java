/*
 * Created on Mar 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.util.List;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;

public final class TigrQualitiesEncodedGyphCodec implements GlyphCodec<PhredQuality>{

    private static TigrQualitiesEncodedGyphCodec INSTANCE = new TigrQualitiesEncodedGyphCodec();
    
    private TigrQualitiesEncodedGyphCodec(){}
    
    public static TigrQualitiesEncodedGyphCodec getINSTANCE(){
        return INSTANCE;
    }
    
    @Override
    public List<PhredQuality> decode(byte[] encodedGlyphs) {       
        return PhredQuality.valueOf(TigrQualitiesEncoder.decode(new String(encodedGlyphs)));       
    }

    @Override
    public PhredQuality decode(byte[] encodedGlyphs, int index) {
        return PhredQuality.valueOf(TigrQualitiesEncoder.decode((char)encodedGlyphs[index]));
    }

    @Override
    public int decodedLengthOf(byte[] encodedGlyphs) {
        return encodedGlyphs.length;
    }

    @Override
    public byte[] encode(List<PhredQuality> glyphs) {
        return TigrQualitiesEncoder.encode(PhredQuality.toArray(glyphs)).getBytes();
    }

}
