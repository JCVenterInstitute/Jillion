package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;

import org.jcvi.common.core.seq.fastx.fasta.aa.impl.LargeAminoAcidSequenceFastaFileDataStore;

public class TestLargeAminoAcidSequenceFastaFileDataStore  extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestLargeAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return LargeAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}


}
