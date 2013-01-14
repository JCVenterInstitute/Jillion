package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.fasta.nt.IndexedNucleotideSequenceFastaFileDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;

public class TestIndexedNucleotideFastaFileDataStore extends AbstractTestSequenceFastaDataStore {

    @Override
    protected DataStore<NucleotideSequenceFastaRecord> parseFile(File file)
            throws IOException {
        return IndexedNucleotideSequenceFastaFileDataStore.create(file);
    }

}
