package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;

public class TestDefaultAminoAcidSequenceFastaDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestDefaultAminoAcidSequenceFastaDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return DefaultAminoAcidSequenceFastaDataStore.create(fastaFile);
	}

}
