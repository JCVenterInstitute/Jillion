/*
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;

public class TestLargeFastQFileDataStore extends AbstractTestFastQFileDataStore{

    @Override
    protected FastQFileVisitor createFastQFileDataStore(File file,
            FastQQualityCodec qualityCodec) {
        return new LargeFastQFileDataStore(file, qualityCodec);
    }

}
