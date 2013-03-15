package org.jcvi.jillion.core.residue.aa;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestIterationOnlyAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaFileDataStore{

	public TestIterationOnlyAminoAcidSequenceFastaFileDataStore() throws IOException {
		super(DataStoreProviderHint.ITERATION_ONLY);
	}

}
