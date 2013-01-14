package org.jcvi.jillion.trace.sanger;

import java.io.File;

import org.jcvi.jillion.trace.sanger.DefaultPositionFastaFileDataStore;
import org.jcvi.jillion.trace.sanger.PositionSequenceFastaDataStore;

public class TestDefaultPositionFastaFileDataStore extends AbstractTestPositionFastaFileDataStore{

	@Override
	protected PositionSequenceFastaDataStore createPositionFastaMap(
			File fastaFile) throws Exception {
		return DefaultPositionFastaFileDataStore.create(fastaFile);
	}

}
