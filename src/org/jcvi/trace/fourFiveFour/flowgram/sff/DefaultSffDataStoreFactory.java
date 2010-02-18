/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultSffDataStoreFactory implements SffDataStoreFactory{

    private final GlyphCodec<PhredQuality> qualityCodec;
    
    /**
     * @param shouldTrim
     * @param qualityCodec
     */
    public DefaultSffDataStoreFactory(GlyphCodec<PhredQuality> qualityCodec) {
        this.qualityCodec = qualityCodec;
    }
    
    /**
     * @param shouldTrim
     * @param qualityCodec
     */
    public DefaultSffDataStoreFactory() {
        this(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
    }

    @Override
    public SffDataStore createDataStoreFor(File sffFile) throws IOException {
        try {
            return new DefaultSffFileDataStore(sffFile, qualityCodec);
        } catch (SFFDecoderException e) {
            throw new IOException("could not parse SFF file",e);
        }
    }

}
