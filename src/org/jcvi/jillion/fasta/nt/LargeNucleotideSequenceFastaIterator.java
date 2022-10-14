/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
final class LargeNucleotideSequenceFastaIterator extends AbstractBlockingStreamingIterator<NucleotideFastaRecord>{

	private final FastaParser parser;
	private final Predicate<String> filter;
	private final Predicate<NucleotideFastaRecord> recordFilter;
	private final InvalidCharacterHandler invalidCharacterHandler;
	
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(File fastaFile, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter) throws IOException{
		 return createNewIteratorFor(FastaFileParser.create(fastaFile), filter, recordFilter, Nucleotide.defaultInvalidCharacterHandler());				                               
	 }
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(File fastaFile, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter, InvalidCharacterHandler invalidCharacterHandler) throws IOException{
		 return createNewIteratorFor(FastaFileParser.create(fastaFile), filter, recordFilter, invalidCharacterHandler);				                               
	 }
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter) throws IOException{
		 return createNewIteratorFor(parser, filter, recordFilter, Nucleotide.defaultInvalidCharacterHandler());
	 }
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter, InvalidCharacterHandler invalidCharacterHandler) throws IOException{
		 LargeNucleotideSequenceFastaIterator iter = new LargeNucleotideSequenceFastaIterator(parser, filter, recordFilter, invalidCharacterHandler);
				                                iter.start();			
	    	
	    	return iter;
	 }
	 
	 private LargeNucleotideSequenceFastaIterator(FastaParser parser, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter,
			 InvalidCharacterHandler invalidCharacterHandler){
		 super(5_000); // fasta records shouldn't be that big...
		 
		 if(!parser.canParse()){
			 throw new IllegalStateException("parser must still be able to parse fasta");
		 }
		 this.parser = parser;
		 this.filter = filter;
		 this.recordFilter = recordFilter;
		 this.invalidCharacterHandler = invalidCharacterHandler;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	    	FastaVisitor visitor = new FastaVisitor(){
	    		NucleotideFastaRecordVisitor recordVisitor = new NucleotideFastaRecordVisitor();
		    	
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
	    	};
	    	
	    	try {
	    		parser.parse(visitor);
			} catch (IOException e) {
				throw new RuntimeException("can not parse fasta file",e);
			}
	    }
	    
	    private class NucleotideFastaRecordVisitor implements FastaRecordVisitor{
			private String currentId;
			private String currentComment;
			//since we are iterating only we probably don't care about compressing the data
			//and initialize the size of the builder to a reasonable size for sequencing reads, it will grow if needed.
			private NucleotideSequenceBuilder builder=  new NucleotideSequenceBuilder(2_000)
															.setInvalidCharacterHandler(invalidCharacterHandler)
															.turnOffDataCompression(true);
			private FastaVisitorCallback callback;
			
			public void prepareNewRecord(FastaVisitorCallback callback, String id, String optionalComment){
				this.currentId = id;
				this.currentComment = optionalComment;
				this.callback = callback;
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
                                    blockingPut(fastaRecord);
                                    if (LargeNucleotideSequenceFastaIterator.this.isClosed()) {
                                        callback.haltParsing();
                                    }
                                }
				
				
			}
			@Override
			public void halted() {
				//no-op				
			}
	    }
}
