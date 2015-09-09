/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

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
	
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(File fastaFile, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter) throws IOException{
		 return createNewIteratorFor(FastaFileParser.create(fastaFile), filter, recordFilter);				                               
	    }
	 
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(FastaParser parser, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter) throws IOException{
		 LargeNucleotideSequenceFastaIterator iter = new LargeNucleotideSequenceFastaIterator(parser, filter, recordFilter);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargeNucleotideSequenceFastaIterator(FastaParser parser, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter){
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
	    		NucleotideFastaRecordVisitor recordVisitor = new NucleotideFastaRecordVisitor(recordFilter);
		    	
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
			private NucleotideSequenceBuilder builder;
			private FastaVisitorCallback callback;
			private final Predicate<NucleotideFastaRecord> recordFilter;
			
			public NucleotideFastaRecordVisitor(Predicate<NucleotideFastaRecord> recordFilter){
			    this.recordFilter = recordFilter;
			}
			public void prepareNewRecord(FastaVisitorCallback callback, String id, String optionalComment){
				this.currentId = id;
				this.currentComment = optionalComment;
				this.callback = callback;
				builder = new NucleotideSequenceBuilder();
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
