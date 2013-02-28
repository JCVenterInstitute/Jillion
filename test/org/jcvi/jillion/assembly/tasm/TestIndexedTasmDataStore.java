package org.jcvi.jillion.assembly.tasm;

import java.io.IOException;

import org.jcvi.jillion.assembly.ctg.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;

public class TestIndexedTasmDataStore extends AbstractTestTasmDataStore{

	static{
		ResourceHelper resources = new ResourceHelper(TestIndexedTasmDataStore.class);

		NucleotideSequenceFastaDataStore fullLengthFastas;
		try {
			fullLengthFastas = new NucleotideSequenceFastaFileDataStoreBuilder(
					resources.getFile("files/giv-15050.fasta")).hint(
					DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
					.build();
			
			contigDataStore = new TigrContigFileDataStoreBuilder(
					resources.getFile("files/giv-15050.contig"),
					fullLengthFastas).build();
			tasmDataStore = new TasmContigFileDataStoreBuilder(resources.getFile("files/giv-15050.tasm"),	fullLengthFastas)
								.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)						
								.build();
		} catch (IOException e) {
			throw new IllegalStateException("error creating datastores",e);
		}
	}

}