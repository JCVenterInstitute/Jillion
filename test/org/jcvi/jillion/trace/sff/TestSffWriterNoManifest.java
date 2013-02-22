package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class TestSffWriterNoManifest extends AbstractTestSffWriter{

	@Override
	protected String getPathToFile() {
		return "files/5readExample_noIndex_noXML.sff";
	}

	@Override
	protected SffFileDataStore createDataStore(File inputSff) throws IOException {
		return new SffFileDataStoreBuilder(inputSff)
									.build();
	}

	@Override
	protected SffWriter createWriter(File outputFile,
			NucleotideSequence keySequence, NucleotideSequence flowSequence) throws IOException {
		return new SffWriterBuilder(outputFile, keySequence, flowSequence)
					.build();
	}

}
