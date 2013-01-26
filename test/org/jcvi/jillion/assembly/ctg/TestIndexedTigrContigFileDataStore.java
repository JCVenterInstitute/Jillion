package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;

public class TestIndexedTigrContigFileDataStore extends AbstractTestContigFileDataStore<TigrContigRead, TigrContig, TigrContigDataStore>{
   
    public TestIndexedTigrContigFileDataStore() throws IOException {
		super();
	}

	@Override
    protected TigrContigDataStore buildContigFileDataStore(
    		NucleotideSequenceFastaDataStore fullLengthSequences, File file) throws IOException {
        return new TigrContigFileDataStoreBuilder(file, fullLengthSequences)
        		.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_MEMORY)
        		.build();
    }

}
