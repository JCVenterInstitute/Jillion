/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;


import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class FastQNucleotideDataStoreAdapter extends AbstractFastQDataStoreAdapter<NucleotideEncodedGlyphs> implements NucleotideDataStore{

    /**
     * @param dataStore
     */
    public FastQNucleotideDataStoreAdapter(DataStore<FastQRecord> dataStore) {
        super(dataStore);
    }

    @Override
    public NucleotideEncodedGlyphs get(String id) throws DataStoreException {
        return getDataStore().get(id).getNucleotides();
    }
   
}
