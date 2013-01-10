package org.jcvi.common.core.seq.fasta.aa;

import java.io.File;

import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.core.internal.seq.fasta.aa.LargeAminoAcidSequenceFastaFileDataStore;

public class TestLargeAminoAcidSequenceFastaFileDataStore  extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestLargeAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return LargeAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}


}
