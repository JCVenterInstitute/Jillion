package org.jcvi.jillion.fasta.nt;

import java.util.function.Predicate;

import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.Sneak;
/**
 * 
 * @author dkatzel
 *
 */
class LargeNucleotideFastaVisitor implements FastaVisitor{

	private final Predicate<String> filter;
	
	NucleotideFastaRecordVisitor recordVisitor;
	
	private final org.jcvi.jillion.core.util.streams.ThrowingBiConsumer<String, NucleotideFastaRecord, ? extends Throwable> consumer;
	
	public LargeNucleotideFastaVisitor( Predicate<String> idFilter, Predicate<NucleotideFastaRecord> recordFilter, 
			InvalidCharacterHandler invalidCharacterHandler,
			org.jcvi.jillion.core.util.streams.ThrowingBiConsumer<String, NucleotideFastaRecord, ? extends Throwable> consumer) {
		filter = idFilter;
		this.consumer = consumer;
		recordVisitor = new NucleotideFastaRecordVisitor(recordFilter, invalidCharacterHandler);
		
	}
	
	@Override
	public FastaRecordVisitor visitDefline(
			final FastaVisitorCallback callback, String id,
			String optionalComment) {
		if(!filter.test(id)){
			return null;
		}
		recordVisitor.prepareNewRecord(callback, id, optionalComment);
		return recordVisitor;
	}

	@Override
	public void visitEnd() {
	//no-op
	}
	@Override
	public void halted() {
		//no-op					
	}
	
	
	private class NucleotideFastaRecordVisitor implements FastaRecordVisitor{
		private String currentId;
		private String currentComment;
		private final NucleotideSequenceBuilder builder;
		
		private final Predicate<NucleotideFastaRecord> recordFilter;
		
		
		public NucleotideFastaRecordVisitor(Predicate<NucleotideFastaRecord> recordFilter, InvalidCharacterHandler invalidCharacterHandler){
		    this.recordFilter = recordFilter;
		  //since we are iterating only we probably don't care about compressing the data
			//and initialize the size of the builder to a reasonable size for sequencing reads, it will grow if needed.
			
		    builder=  new NucleotideSequenceBuilder(2_000)
					.setInvalidCharacterHandler(invalidCharacterHandler)
					.turnOffDataCompression(true);
		    
		}
		public void prepareNewRecord(FastaVisitorCallback callback, String id, String optionalComment){
			this.currentId = id;
			this.currentComment = optionalComment;
		
			//clear the length instead of new object for each visit
			builder.clear();
		}
		@Override
		public void visitBodyLine(String line) {
			builder.append(line);
			
		}

		@Override
		public void visitEnd() {
			NucleotideFastaRecord fastaRecord = new NucleotideFastaRecordBuilder(currentId,builder.build())
													.comment(currentComment)
													.build();
			
                            if (recordFilter == null || recordFilter.test(fastaRecord)) {
                            	try {
									consumer.accept(fastaRecord.getId(), fastaRecord);
								} catch (Throwable e) {
									Sneak.sneakyThrow(e);
								}
                                
                            }
			
			
		}
		@Override
		public void halted() {
			//no-op				
		}
    }
}
