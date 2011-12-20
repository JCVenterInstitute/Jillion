package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.File;
import java.io.IOException;

public class TestPositionFastaFileDataStore extends AbstractTestPositionsFastaDataStore{


    @Override
    protected PositionFastaDataStore createPositionFastaMap(File fastaFile) throws IOException {
        return IndexedPositionFastaFileDataStore.create(fastaFile);
    }

}
