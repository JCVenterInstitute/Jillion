/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class H2QualityFastQDataStore extends AbstractH2FastQDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> implements QualityDataStore{
    
    /**
     * @param fastQFile
     * @param qualityCodec
     * @param datastore
     * @throws FileNotFoundException
     */
    public H2QualityFastQDataStore(
            File fastQFile,
            FastQQualityCodec qualityCodec,
            AbstractH2EncodedGlyphDataStore<PhredQuality, EncodedGlyphs<PhredQuality>> datastore)
            throws FileNotFoundException {
        super(fastQFile, qualityCodec, datastore);

    }

    @Override
    public boolean visitEncodedQualities(String encodedQualities) {
        StringBuilder builder = new StringBuilder();
        for(PhredQuality quality :this.getQualityCodec().decode(encodedQualities).decode()){
            builder.append(Integer.valueOf(quality.getNumber()))
                            .append(" ");
        }
                
               
        try {
            this.getDatastore().insertRecord(this.getCurrentId(),
                    builder.toString());
        } catch (DataStoreException e) {
          throw new IllegalStateException("could not insert qualities for into datastore",e);
        }
        return true;
    }


}
