/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
final class LargeNucleotideSequenceFastaIterator extends AbstractBlockingStreamingIterator<NucleotideSequenceFastaRecord>{

	private final File fastaFile;
	private final DataStoreFilter filter;
	
	 public static LargeNucleotideSequenceFastaIterator createNewIteratorFor(File fastaFile, DataStoreFilter filter){
		 LargeNucleotideSequenceFastaIterator iter = new LargeNucleotideSequenceFastaIterator(fastaFile, filter);
				                                iter.start();			
	    	
	    	return iter;
	    }
	 
	 private LargeNucleotideSequenceFastaIterator(File fastaFile, DataStoreFilter filter){
		 this.fastaFile = fastaFile;
		 this.filter = filter;
	 }
	 /**
	    * {@inheritDoc}
	    */
	    @Override
	    protected void backgroundThreadRunMethod() {
	    	FastaFileVisitor visitor = new FastaFileVisitor(){
	    		NucleotideFastaRecordVisitor recordVisitor = new NucleotideFastaRecordVisitor();
		    	
				@Override
				public FastaRecordVisitor visitDefline(
						final FastaVisitorCallback callback, String id,
						String optionalComment) {
					if(!filter.accept(id)){
						return null;
					}
					recordVisitor.prepareNewRecord(callback, id, optionalComment);
					return recordVisitor;
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
	    
	    private class NucleotideFastaRecordVisitor implements FastaRecordVisitor{
			private String currentId;
			private String currentComment;
			private NucleotideSequenceBuilder builder;
			private FastaVisitorCallback callback;
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
				NucleotideSequenceFastaRecord fastaRecord = new NucleotideSequenceFastaRecordBuilder(currentId,builder.build())
														.comment(currentComment)
														.build();
				blockingPut(fastaRecord);
				if(LargeNucleotideSequenceFastaIterator.this.isClosed()){
					callback.stopParsing();
				}
				
			}
		    	
	    }
}
