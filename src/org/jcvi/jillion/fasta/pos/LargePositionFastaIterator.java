package org.jcvi.jillion.fasta.pos;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class LargePositionFastaIterator  extends AbstractBlockingStreamingIterator<PositionFastaRecord>{

	private final FastaParser parser;
	private final Predicate<String> filter;
	private final Predicate<PositionFastaRecord> recordFilter;
	
	public static StreamingIterator<PositionFastaRecord> createNewIteratorFor(File fastaFile) throws IOException {
		return createNewIteratorFor(fastaFile, id->true, null);
	}
	 public static LargePositionFastaIterator createNewIteratorFor(File fastaFile, Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter) throws IOException{
		 return createNewIteratorFor(FastaFileParser.create(fastaFile), filter, recordFilter);				                               
	    }
	 
	 public static LargePositionFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter) throws IOException{
		 LargePositionFastaIterator iter = new LargePositionFastaIterator(parser, filter, recordFilter);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargePositionFastaIterator(FastaParser parser, Predicate<String> filter, Predicate<PositionFastaRecord> recordFilter){
		 if(!parser.canParse()){
			 throw new IllegalStateException("parser must still be able to parse fasta");
		 }
		 this.parser = parser;
		 this.filter = filter;
		 this.recordFilter = recordFilter;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	    	FastaVisitor visitor = new FastaVisitor(){
	    		
				@Override
				public FastaRecordVisitor visitDefline(
						final FastaVisitorCallback callback, String id,
						String optionalComment) {
					if(!filter.test(id)){
						return null;
					}
					
					return new AbstractPositionSequenceFastaRecordVisitor(id,optionalComment){

						@Override
						protected void visitRecord(
								PositionFastaRecord fastaRecord) {
							if(recordFilter ==null || recordFilter.test(fastaRecord)){
								 blockingPut(fastaRecord);
                                 if (LargePositionFastaIterator.this.isClosed()) {
                                     callback.haltParsing();
                                 }
							}
							
						}
						
					};
				}

				@Override
				public void visitEnd() {
					//no-op					
				}
				@Override
				public void halted() {
					//no-op					
				}
	    	};
	    	
	    	try {
	    		parser.parse(visitor);
			} catch (IOException e) {
				throw new RuntimeException("can not parse fasta file",e);
			}
	    }

	
	    
	  
}
