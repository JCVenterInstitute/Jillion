package org.jcvi.jillion.fasta.aa;

import java.io.File;

import org.jcvi.jillion.core.internal.seq.fasta.aa.DefaultAminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;

public class TestDefaultAminoAcidSequenceFastaDataStore extends AbstractTestAminoAcidSequenceFastaDataStore{


	public TestDefaultAminoAcidSequenceFastaDataStore() throws Exception {
		super();
	}

	@Override
	protected AminoAcidSequenceFastaDataStore create(File fastaFile) throws Exception{
		return DefaultAminoAcidSequenceFastaDataStore.create(fastaFile);
	}

}
