package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.IOException;

public interface ProteinFastaFileDataStore extends ProteinFastaDataStore{

    public static ProteinFastaFileDataStore fromFile(File fasta) throws IOException{
        return new ProteinFastaFileDataStoreBuilder(fasta).build();
    }
}
