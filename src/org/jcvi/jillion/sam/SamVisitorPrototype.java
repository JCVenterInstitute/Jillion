package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.sam.header.SamHeader;

public class SamVisitorPrototype implements SamVisitor{

	private int numberOfRecords = 0;
	
	@Override
	public void visitHeader(SamHeader header) {
		System.out.println(header);
		
		System.out.println("\n\n\n\n\n==================================\n");
	}

	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	@Override
	public void visitRecord(SamRecord record) {
		System.out.println("record  " + record.getQueryName());
		if(record.isPrimary()){
			System.out.println("primary");
		}else{
			System.out.println("not primary");
		}
		System.out.println("cigar = " + record.getCigar());
		System.out.println("seq = " + record.getSequence());
		numberOfRecords ++;
	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) throws IOException{
		
		/*File sam = new File("/local/netapp_scratch/dkatzel/samFluPrototype/giv2_SGB2_48129_hybrid_edited_refs_33.sam");
	
		SamFileParser parser = new SamFileParser(sam);
		
		File bam = new File("/local/netapp_scratch/dkatzel/samFluPrototype/giv2_SGB2_48129_hybrid_edited_refs_33.bam");
		BamFileParser parser = new BamFileParser(bam);
		*/
		//File f = new File("/usr/local/scratch/dkatzel/output2.bam");
		//File f = new File("/usr/local/scratch/dkatzel/output3.bam");
		File f = new File("/usr/local/scratch/dkatzel/picard.bam");
	//	File f = new File("/usr/local/scratch/dkatzel/expected2.bam");
		SamParser parser = SamParserFactory.create(f);
		SamVisitorPrototype visitor = new SamVisitorPrototype();
	//	PrintStream out = new PrintStream(new FileOutputStream("/usr/local/scratch/dkatzel/BAM.visit.out5"), true);
	//	System.setOut(out);
		parser.accept(visitor);
		System.out.println("found " + visitor.getNumberOfRecords());
	//	out.close();
	}
}
