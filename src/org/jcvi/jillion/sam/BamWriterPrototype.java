package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.sam.header.SamHeader;

public class BamWriterPrototype {

	public static void main(String[] args) throws IOException {
	//	File inputBam = new File("/local/devel/VIRIFX/users/dkatzel/eclipseWorkspace/Jillion/test/org/jcvi/jillion/sam/example.bam");
		
	//	final File outputBam = new File("/usr/local/scratch/dkatzel/output.bam");
		
		File inputBam = new File("/local/devel/VIRIFX/users/dkatzel/eclipseWorkspace/Jillion/test/org/jcvi/jillion/sam/example.sam");
		
		//File inputBam = new File("/usr/local/scratch/dkatzel/output.bam");
		
		final File outputBam = new File("/usr/local/scratch/dkatzel/output.qnameResort.1.sam");
		
		
		
		SamParser parser = SamParserFactory.create(inputBam);
		final List<SamRecord> records = new ArrayList<SamRecord>();
		
		parser.accept(new SamVisitor() {
			private SamWriter writer;
			
			
			@Override
			public void visitHeader(SamHeader header) {
				try {
					writer = new SamWriterBuilder(outputBam, header)
									.reSortBy(SortOrder.QUERY_NAME, 1)
									.build();
				} catch (IOException e) {
					throw new IllegalStateException("error creating new bam writer", e);
				}
				
				
			}
			
			@Override
			public void visitRecord(SamRecord record) {
				try {
					writer.writeRecord(record);
					records.add(record);
					
				} catch (IOException e) {
					throw new IllegalStateException("error writing record "+ record, e);
				}
			}
			
			@Override
			public void visitEnd() {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});

		for(SamRecord r : records){
			System.out.println(r);
		}
		
	}

}
