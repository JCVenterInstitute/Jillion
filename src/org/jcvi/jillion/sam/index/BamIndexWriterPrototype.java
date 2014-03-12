package org.jcvi.jillion.sam.index;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.VirtualFileOffset;
import org.jcvi.jillion.sam.header.SamHeader;

public class BamIndexWriterPrototype {

	public static void main(String[] args) throws IOException {
		File bam = new File("/export/picard/picardsource/trunk/testdata/net/sf/samtools/BAMFileIndexTest/index_test.bam");
		File expectedIndex = new File("/export/picard/picardsource/trunk/testdata/net/sf/samtools/BAMFileIndexTest/index_test.bam.bai");
		
		SamParserFactory.create(bam)
				.accept(new SamVisitor() {
					BamIndexer indexer;
					@Override
					public void visitRecord(SamRecord record) {
						//no-op
						
					}
					
					
					
					@Override
					public void visitRecord(SamRecord record,
							VirtualFileOffset start, VirtualFileOffset end) {
						indexer.addRecord(record, start, end);
						
					}



					@Override
					public void visitHeader(SamHeader header) {
							
							indexer = new BamIndexer(header);
						
					}
					
					@Override
					public void visitEnd() {
						File output = new File("/usr/local/scratch/dkatzel/testBamWriterWithIndex/jillion.index_test_fixed.bai");
						OutputStream out = null;
						try{
							out = new BufferedOutputStream(new FileOutputStream(output));
							IndexUtil.writeIndex(out, indexer.createReferenceIndexes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							IOUtil.closeAndIgnoreErrors(out);
						}
						
					}
				});
		
		File actualIndex = new File("/usr/local/scratch/dkatzel/testBamWriterWithIndex/jillion.index_test.bai");
		System.out.println(Arrays.equals(IOUtil.toByteArray(expectedIndex),
				IOUtil.toByteArray(actualIndex)));
		

	}

}
