/*
 * Created on Dec 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Arrays;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;


public class ArtificalQualityDataStoreFromContig extends AbstractArtificialDataStoreFromContig<EncodedGlyphs<PhredQuality>> implements QualityDataStore{
    private final byte qualitytoUse;
    
    public ArtificalQualityDataStoreFromContig(
            DataStore<? extends Contig> contigDataStore, PhredQuality qualityToUse) {
        super(contigDataStore);
        this.qualitytoUse = qualityToUse.getNumber().byteValue();
    }

   
    
    @Override
    protected EncodedGlyphs<PhredQuality> createArtificalTypefor(PlacedRead read) {
        long length =read.getValidRange().getEnd()+1;
       byte[] buf = new byte[(int)length];
       Arrays.fill(buf, qualitytoUse);
        return new DefaultEncodedGlyphs<PhredQuality>(
                RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, PhredQuality.valueOf(buf));

    }

}
