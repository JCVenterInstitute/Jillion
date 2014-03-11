package org.jcvi.jillion.sam.index;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.sam.SamParserFactory;
import org.jcvi.jillion.sam.SamRecord;
import org.jcvi.jillion.sam.SamVisitor;
import org.jcvi.jillion.sam.SamWriter;
import org.jcvi.jillion.sam.SamWriterBuilder;
import org.jcvi.jillion.sam.header.SamHeader;

public class BamIndexWriterPrototype {

	public static void main(String[] args) throws IOException {
		File bam = new File("/export/picard/picardsource/trunk/testdata/net/sf/samtools/BAMFileIndexTest/index_test.bam");
		File expectedIndex = new File("/export/picard/picardsource/trunk/testdata/net/sf/samtools/BAMFileIndexTest/index_test.bam.bai");
		
		SamParserFactory.create(bam)
				.accept(new SamVisitor() {
					SamWriter writer=null;
					@Override
					public void visitRecord(SamRecord record) {
						try {
							writer.writeRecord(record);
						} catch (IOException e) {
							throw new IllegalStateException(e);	
						}
						
					}
					
					@Override
					public void visitHeader(SamHeader header) {
						try {
							writer = new SamWriterBuilder(
									new File("/usr/local/scratch/dkatzel/testBamWriterWithIndex/jillion.index_test.bam")
									, header)
								.createBamIndex(true)
								.build();
						} catch (IOException e) {
							throw new IllegalStateException(e);						
						}
					}
					
					@Override
					public void visitEnd() {
						try {
							writer.close();
						} catch (IOException e) {
							throw new IllegalStateException(e);	
						}
						
					}
				});
		
		File actualIndex = new File("/usr/local/scratch/dkatzel/testBamWriterWithIndex/jillion.index_test.bai");
		System.out.println(Arrays.equals(IOUtil.toByteArray(expectedIndex),
				IOUtil.toByteArray(actualIndex)));
		

	}

}
