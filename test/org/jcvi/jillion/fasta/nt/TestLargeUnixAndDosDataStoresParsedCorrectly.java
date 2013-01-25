package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

public class TestLargeUnixAndDosDataStoresParsedCorrectly extends AbstractTestUnixAndDosFormatsParsedCorrectly{

	public TestLargeUnixAndDosDataStoresParsedCorrectly() throws IOException {
		super();
	}

	@Override
	protected NucleotideSequenceFastaDataStore createDataStoreFor(File fastaFile)
			throws IOException {
		return LargeNucleotideSequenceFastaFileDataStore.create(fastaFile);
	}

}
