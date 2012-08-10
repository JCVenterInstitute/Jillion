package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.File;

public class TestDefaultPositionFastaFileDataStore extends AbstractTestPositionFastaFileDataStore{

	@Override
	protected PositionSequenceFastaDataStore createPositionFastaMap(
			File fastaFile) throws Exception {
		return DefaultPositionFastaFileDataStore.create(fastaFile);
	}

}
