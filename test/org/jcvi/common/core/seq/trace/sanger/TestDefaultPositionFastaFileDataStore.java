package org.jcvi.common.core.seq.trace.sanger;

import java.io.File;

import org.jcvi.common.core.seq.trace.sanger.DefaultPositionFastaFileDataStore;
import org.jcvi.common.core.seq.trace.sanger.PositionSequenceFastaDataStore;

public class TestDefaultPositionFastaFileDataStore extends AbstractTestPositionFastaFileDataStore{

	@Override
	protected PositionSequenceFastaDataStore createPositionFastaMap(
			File fastaFile) throws Exception {
		return DefaultPositionFastaFileDataStore.create(fastaFile);
	}

}
