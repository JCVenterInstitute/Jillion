package org.jcvi.jillion.fasta.aa;

import java.io.File;

import org.jcvi.jillion.core.internal.seq.fasta.aa.IndexedAminoAcidSequenceFastaFileDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;

public class TestIndexedAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestIndexedAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return IndexedAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}
}
