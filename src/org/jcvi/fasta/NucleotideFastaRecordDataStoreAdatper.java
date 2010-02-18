/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class NucleotideFastaRecordDataStoreAdatper<F extends FastaRecord<NucleotideEncodedGlyphs>> extends FastaRecordDataStoreAdapter<NucleotideEncodedGlyphs,F> implements NucleotideDataStore{
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param <F> a {@code FastaRecord<Nucleotide>}.
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code DataStore<T>} which wraps the given datastore. 
     */
    public static <F extends FastaRecord<NucleotideEncodedGlyphs>> NucleotideFastaRecordDataStoreAdatper adapt(DataStore<F> datastoreOfFastaRecords){
        return new NucleotideFastaRecordDataStoreAdatper<F>(datastoreOfFastaRecords);
    }
    private NucleotideFastaRecordDataStoreAdatper(
            DataStore<F> datastoreOfFastaRecords) {
        super(datastoreOfFastaRecords);
    }

}
