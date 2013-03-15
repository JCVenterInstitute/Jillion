package org.jcvi.jillion.core.residue.aa;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestMementoAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaFileDataStore{

	public TestMementoAminoAcidSequenceFastaFileDataStore() throws IOException {
		super(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS);
	}

}
