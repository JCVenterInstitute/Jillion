package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

public class TestDefaultUnixAndDosDataStoresParsedCorrectly extends AbstractTestUnixAndDosFormatsParsedCorrectly{

	public TestDefaultUnixAndDosDataStoresParsedCorrectly() throws IOException {
		super();
	}

	@Override
	protected NucleotideSequenceFastaDataStore createDataStoreFor(File fastaFile)
			throws IOException {
		return DefaultNucleotideSequenceFastaFileDataStore.create(fastaFile);
	}

}
