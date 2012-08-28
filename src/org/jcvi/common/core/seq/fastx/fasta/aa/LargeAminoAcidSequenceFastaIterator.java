package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;

public final class LargeAminoAcidSequenceFastaIterator extends AbstractBlockingCloseableIterator<AminoAcidSequenceFastaRecord>{

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
					AminoAcidSequenceFastaRecord fastaRecord = AminoAcidSequenceFastaRecordFactory.create(id, new AminoAcidSequenceBuilder(entireBody).build(),comment);
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
