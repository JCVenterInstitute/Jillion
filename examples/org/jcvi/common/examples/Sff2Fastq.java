package org.jcvi.common.examples;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.trace.sff.SffFileDataStore;
import org.jcvi.jillion.trace.sff.SffFileDataStoreBuilder;

public class Sff2Fastq {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File sffFile = new File("/path/to/input.sff");
		File fastqFile = new File("/path/to/output.fastq");
		
		SffFileDataStore sffDataStore = new SffFileDataStoreBuilder(sffFile)
												.hint(DataStoreProviderHint.ITERATION_ONLY)
												.build();
		
		

	}

}
