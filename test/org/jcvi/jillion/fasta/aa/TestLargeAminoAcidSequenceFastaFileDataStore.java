package org.jcvi.jillion.fasta.aa;

import java.io.File;

import org.jcvi.jillion.core.internal.seq.fasta.aa.LargeAminoAcidSequenceFastaFileDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;

public class TestLargeAminoAcidSequenceFastaFileDataStore  extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestLargeAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return LargeAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}


}
