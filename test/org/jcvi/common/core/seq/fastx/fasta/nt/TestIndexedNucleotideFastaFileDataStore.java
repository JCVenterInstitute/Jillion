package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.IndexedNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecord;

public class TestIndexedNucleotideFastaFileDataStore extends AbstractTestSequenceFastaDataStore {

    @Override
    protected DataStore<NucleotideSequenceFastaRecord> parseFile(File file)
            throws IOException {
        return IndexedNucleotideFastaFileDataStore.create(file);
    }

}
