package org.jcvi.jillion.fasta.pos;

import java.io.File;

import org.jcvi.jillion.fasta.FastaFileParser;

public class TestLargePositionFastaFileDataStore extends AbstractTestPositionFastaFileDataStore{

	@Override
	protected PositionFastaDataStore createPositionFastaMap(
			File fastaFile) throws Exception {
		return LargePositionFastaFileDataStore.create(FastaFileParser.create(fastaFile));
	}

}
