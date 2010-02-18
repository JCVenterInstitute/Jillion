/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class FastQQualitiesDataStoreAdapter extends AbstractFastQDataStoreAdapter<EncodedGlyphs<PhredQuality>> implements QualityDataStore{
    /**
     * @param dataStore
     */
    public FastQQualitiesDataStoreAdapter(DataStore<FastQRecord> dataStore) {
        super(dataStore);
    }

    @Override
    public EncodedGlyphs<PhredQuality> get(String id) throws DataStoreException {
        return getDataStore().get(id).getQualities();
    }
   
}
