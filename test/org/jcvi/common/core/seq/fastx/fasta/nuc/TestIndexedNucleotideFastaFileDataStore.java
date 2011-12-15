package org.jcvi.common.core.seq.fastx.fasta.nuc;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;

public class TestIndexedNucleotideFastaFileDataStore extends AbstractTestSequenceFastaDataStore {

    @Override
    protected DataStore<NucleotideSequenceFastaRecord> parseFile(File file)
            throws IOException {
        return IndexedNucleotideFastaFileDataStore.create(file);
    }

}
