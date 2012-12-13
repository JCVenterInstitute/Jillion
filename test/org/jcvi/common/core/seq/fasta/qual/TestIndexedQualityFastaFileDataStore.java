package org.jcvi.common.core.seq.fasta.qual;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.seq.fasta.qual.IndexedQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaDataStore;


public class TestIndexedQualityFastaFileDataStore extends AbstractTestQualityFastaDataStore{
    
    @Override
    protected QualitySequenceFastaDataStore createDataStore(File file) throws IOException{
        return IndexedQualityFastaFileDataStore.create(file);
    }

}
