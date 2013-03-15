package org.jcvi.jillion.core.residue.aa;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestDefaultAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaFileDataStore{

	public TestDefaultAminoAcidSequenceFastaFileDataStore() throws IOException {
		super(DataStoreProviderHint.OPTIMIZE_FAST_RANDOM_ACCESS);
	}

}
