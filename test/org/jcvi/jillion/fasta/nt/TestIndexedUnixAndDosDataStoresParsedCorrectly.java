package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

public class TestIndexedUnixAndDosDataStoresParsedCorrectly extends AbstractTestUnixAndDosFormatsParsedCorrectly{

	public TestIndexedUnixAndDosDataStoresParsedCorrectly() throws IOException {
		super();
	}

	@Override
	protected NucleotideSequenceFastaDataStore createDataStoreFor(File fastaFile)
			throws IOException {
		return IndexedNucleotideSequenceFastaFileDataStore.create(fastaFile);
	}

}
