package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class TestSffWriterWithIndex extends AbstractTestSffWriter{

	@Override
	protected String getPathToFile() {
		return "files/5readExample_noXML.sff";
	}

	@Override
	protected FlowgramDataStore createDataStore(File inputSff)
			throws IOException {
		return new SffFileDataStoreBuilder(inputSff)
						.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
						.build();
		
	}

	@Override
	protected SffWriter createWriter(File outputFile,
			NucleotideSequence keySequence, NucleotideSequence flowSequence)
			throws IOException {
		return new SffWriterBuilder(outputFile, keySequence, flowSequence)
					.includeIndex(true)
					.build();
	}

}
