/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractAminoAcidFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecord;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

final class LargeAminoAcidSequenceFastaIterator extends AbstractBlockingStreamingIterator<AminoAcidFastaRecord>{

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
	    	FastaVisitor visitor = new FastaVisitor(){

				@Override
				public FastaRecordVisitor visitDefline(
						final FastaVisitorCallback callback, String id,
						String optionalComment) {
					if(!filter.accept(id)){
						return null;
					}
					
					return new AbstractAminoAcidFastaRecordVisitor(id, optionalComment) {
						
						@Override
						protected void visitRecord(AminoAcidFastaRecord fastaRecord) {
							blockingPut(fastaRecord);
							if(LargeAminoAcidSequenceFastaIterator.this.isClosed()){
								callback.haltParsing();
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
				FastaFileParser.create(fastaFile).accept(visitor);
			} catch (IOException e) {
				throw new RuntimeException("can not parse fasta file",e);
			}
	    }
}
