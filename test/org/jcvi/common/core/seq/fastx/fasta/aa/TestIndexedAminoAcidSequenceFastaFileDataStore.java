package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;

public class TestIndexedAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestIndexedAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return IndexedAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}
}
