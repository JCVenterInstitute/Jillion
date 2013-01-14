package org.jcvi.jillion.fasta.aa;

import java.io.File;

import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.internal.fasta.aa.IndexedAminoAcidSequenceFastaFileDataStore;

public class TestIndexedAminoAcidSequenceFastaFileDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestIndexedAminoAcidSequenceFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return IndexedAminoAcidSequenceFastaFileDataStore.create(fastaFile);
	}
}
