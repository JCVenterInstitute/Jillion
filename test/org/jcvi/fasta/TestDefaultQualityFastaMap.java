/*
 * Created on Apr 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class TestDefaultQualityFastaMap extends AbstractTestQualityFastaMap{
    
    @Override
    protected DataStore<QualityFastaRecord<EncodedGlyphs<PhredQuality>>> buildQualityFastaMapFrom(File file) throws IOException{
        return new DefaultQualityFastaFileDataStore(file);
    }
}
