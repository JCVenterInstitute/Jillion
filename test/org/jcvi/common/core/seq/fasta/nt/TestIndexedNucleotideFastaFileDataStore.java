package org.jcvi.common.core.seq.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.fasta.nt.IndexedNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;

public class TestIndexedNucleotideFastaFileDataStore extends AbstractTestSequenceFastaDataStore {

    @Override
    protected DataStore<NucleotideSequenceFastaRecord> parseFile(File file)
            throws IOException {
        return IndexedNucleotideSequenceFastaFileDataStore.create(file);
    }

}
