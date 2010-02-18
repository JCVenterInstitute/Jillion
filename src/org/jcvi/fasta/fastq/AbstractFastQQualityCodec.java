/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.nio.ByteBuffer;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractFastQQualityCodec implements FastQQualityCodec{
    private final GlyphCodec<PhredQuality> qualityCodec;
    public AbstractFastQQualityCodec(GlyphCodec<PhredQuality> qualityCodec){
        this.qualityCodec = qualityCodec;
    }
    @Override
    public EncodedGlyphs<PhredQuality> decode(String fastqQualities) {
        ByteBuffer buffer = ByteBuffer.allocate(fastqQualities.length());
        for(int i=0; i<fastqQualities.length(); i++){
            buffer.put(decode(fastqQualities.charAt(i)).getNumber());
        }
        return new DefaultEncodedGlyphs<PhredQuality>(
                                    qualityCodec,
                                    PhredQuality.valueOf(buffer.array()));
    }

    @Override
    public String encode(EncodedGlyphs<PhredQuality> qualities) {
        StringBuilder builder= new StringBuilder();
        for(PhredQuality quality : qualities.decode()){
            builder.append(encode(quality));
        }
        return builder.toString();
    }
    
    protected abstract PhredQuality decode(char encodedQuality);
    protected abstract char encode(PhredQuality quality);
}
