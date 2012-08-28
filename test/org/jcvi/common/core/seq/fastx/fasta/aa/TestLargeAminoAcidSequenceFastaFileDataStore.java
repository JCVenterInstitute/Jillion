package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;

public class TestLargeAminoAcidSequenceFastaFileDataStore  extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestLargeAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return LargeAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}


}
