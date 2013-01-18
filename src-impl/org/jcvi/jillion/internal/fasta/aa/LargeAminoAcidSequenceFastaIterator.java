package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractAminoAcidFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class LargeAminoAcidSequenceFastaIterator extends AbstractBlockingStreamingIterator<AminoAcidSequenceFastaRecord>{

	private final File fastaFile;
	private final DataStoreFilter filter;
	public static LargeAminoAcidSequenceFastaIterator createNewIteratorFor(File fastaFile){
		return createNewIteratorFor(fastaFile, DataStoreFilters.alwaysAccept());
	}
	 public static LargeAminoAcidSequenceFastaIterator createNewIteratorFor(File fastaFile, DataStoreFilter filter){
		 LargeAminoAcidSequenceFastaIterator iter = new LargeAminoAcidSequenceFastaIterator(fastaFile, filter);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargeAminoAcidSequenceFastaIterator(File fastaFile,DataStoreFilter filter){
		 this.fastaFile = fastaFile;
		 this.filter = filter;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	    	FastaFileVisitor visitor = new FastaFileVisitor(){

				@Override
				public FastaRecordVisitor visitDefline(
						final FastaVisitorCallback callback, String id,
						String optionalComment) {
					if(!filter.accept(id)){
						return null;
					}
					
					return new AbstractAminoAcidFastaRecordVisitor(id, optionalComment) {
						
						@Override
						protected void visitRecord(AminoAcidSequenceFastaRecord fastaRecord) {
							blockingPut(fastaRecord);
							if(LargeAminoAcidSequenceFastaIterator.this.isClosed()){
								callback.stopParsing();
							}
							
						}
					};
				}

				@Override
				public void visitEnd() {
					//no-op
					
				}
	    		
	    	};
	    	
	    	try {
				new FastaFileParser(fastaFile).accept(visitor);
			} catch (IOException e) {
				throw new RuntimeException("can not parse fasta file",e);
			}
	    }
}
