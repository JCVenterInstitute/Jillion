/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class QualityFastaRecordDataStoreAdapter <F extends FastaRecord<EncodedGlyphs<PhredQuality>>> extends FastaRecordDataStoreAdapter<EncodedGlyphs<PhredQuality>,F> implements QualityDataStore{
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param <F> a {@code FastaRecord<Nucleotide>}.
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code DataStore<T>} which wraps the given datastore. 
     */
    public static <F extends QualityFastaRecord<EncodedGlyphs<PhredQuality>>> QualityFastaRecordDataStoreAdapter adapt(DataStore<F> datastoreOfFastaRecords){
        return new QualityFastaRecordDataStoreAdapter<F>(datastoreOfFastaRecords);
    }
    private QualityFastaRecordDataStoreAdapter(
            DataStore<F> datastoreOfFastaRecords) {
        super(datastoreOfFastaRecords);
    }

}
