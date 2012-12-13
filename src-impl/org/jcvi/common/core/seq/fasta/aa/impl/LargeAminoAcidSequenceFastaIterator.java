package org.jcvi.common.core.seq.fasta.aa.impl;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.seq.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaRecordBuilder;
import org.jcvi.common.core.util.iter.impl.AbstractBlockingStreamingIterator;

final class LargeAminoAcidSequenceFastaIterator extends AbstractBlockingStreamingIterator<AminoAcidSequenceFastaRecord>{

	private final File fastaFile;
	
	 public static LargeAminoAcidSequenceFastaIterator createNewIteratorFor(File fastaFile){
		 LargeAminoAcidSequenceFastaIterator iter = new LargeAminoAcidSequenceFastaIterator(fastaFile);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargeAminoAcidSequenceFastaIterator(File fastaFile){
		 this.fastaFile = fastaFile;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	        FastaFileVisitor visitor = new AbstractFastaVisitor() {
				
				@Override
				protected boolean visitRecord(String id, String comment, String entireBody) {
					AminoAcidSequenceFastaRecord fastaRecord = new AminoAcidSequenceFastaRecordBuilder(id, entireBody)
																	.comment(comment)
																	.build();
					blockingPut(fastaRecord);
	                return !LargeAminoAcidSequenceFastaIterator.this.isClosed();
				}
			};
	        try {
	            FastaFileParser.parse(fastaFile, visitor);
	        } catch (FileNotFoundException e) {
	            throw new RuntimeException("fasta file does not exist",e);
	        }
	        
	    }
}
