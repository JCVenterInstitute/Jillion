package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.IOException;



public class TestIndexedQualityFastaFileDataStore extends AbstractTestQualityFastaDataStore{
    
    @Override
    protected QualitySequenceFastaDataStore createDataStore(File file) throws IOException{
        return IndexedQualityFastaFileDataStore.create(file);
    }

}