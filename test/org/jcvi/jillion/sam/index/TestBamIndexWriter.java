package org.jcvi.jillion.sam.index;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestBamIndexWriter {
	ResourceHelper resources = new ResourceHelper(TestBamIndexWriter.class);
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Test
	public void writterWithMetaDataMatchesByteForByteToPicard() throws IOException{
		File bamfile = resources.getFile("index_test.bam");
		File expectedBaiFile = resources.getFile("index_test.bam.bai");
		
		File actualBaiFile = tmpDir.newFile("actual.bai");
		
		new BamIndexFileWriterBuilder(bamfile, actualBaiFile)
					.includeMetaData(true)
					.build();
		
		TestUtil.contentsAreEqual(expectedBaiFile, actualBaiFile);
	}
}
