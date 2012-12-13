package org.jcvi.common.core.seq.fasta.aa;

import java.io.File;

import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.aa.impl.IndexedAminoAcidSequenceFastaFileDataStore;

public class TestIndexedAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestIndexedAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return IndexedAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}
}
