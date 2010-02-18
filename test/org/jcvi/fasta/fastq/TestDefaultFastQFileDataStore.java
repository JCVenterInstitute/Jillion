/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;

public class TestDefaultFastQFileDataStore extends AbstractTestFastQFileDataStore{
    @Override
    protected FastQFileVisitor createFastQFileDataStore(File file, FastQQualityCodec qualityCodec) {
        return new DefaultFastQFileDataStore(qualityCodec);
    }
    
}
