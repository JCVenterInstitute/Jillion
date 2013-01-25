package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;

public class TestDefaultTigrContigFileDataStore extends AbstractTestContigFileDataStore<TigrContigRead, TigrContig, TigrContigDataStore>{
   
    public TestDefaultTigrContigFileDataStore() throws IOException {
		super();
	}

	@Override
    protected TigrContigDataStore buildContigFileDataStore(
    		NucleotideSequenceFastaDataStore fullLengthSequences, File file) throws IOException {
        return new TigrContigFileDataStoreBuilder(file, fullLengthSequences)
        		.build();
    }

}
