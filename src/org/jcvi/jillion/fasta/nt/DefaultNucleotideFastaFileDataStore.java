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
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
/**
 * {@code DefaultNucleotideFastaFileDataStore} is the default implementation
 * of {@link NucleotideSequenceFastaDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeNucleotideSequenceFastaFileDataStore
 *
 */
final class DefaultNucleotideFastaFileDataStore{
	
	private DefaultNucleotideFastaFileDataStore(){
		//can not instantiate.
	}

	private static NucleotideFastaDataStoreBuilderVisitorImpl2 createBuilder(DataStoreFilter filter){
		return new NucleotideFastaDataStoreBuilderVisitorImpl2(filter);
	}
	
	public static NucleotideFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile,DataStoreFilters.alwaysAccept());
	}
	public static NucleotideFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		
		FastaParser parser = FastaFileParser.create(fastaFile);
		
		return create(parser, filter);
	}

	public static NucleotideFastaDataStore create(FastaParser parser,
			DataStoreFilter filter) throws IOException {
		NucleotideFastaDataStoreBuilderVisitorImpl2 builder = createBuilder(filter);
		parser.parse(builder);
		return builder.build();
	}
	
	public static NucleotideFastaDataStore create(InputStream in) throws IOException{
		return create(in,DataStoreFilters.alwaysAccept());
	}
	public static NucleotideFastaDataStore create(InputStream in, DataStoreFilter filter) throws IOException{
		try{
			NucleotideFastaDataStoreBuilderVisitorImpl2 builder = createBuilder(filter);
			FastaFileParser.create(in).parse(builder);
			return builder.build();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
    

    
    private static final class NucleotideFastaDataStoreBuilderVisitorImpl2 implements FastaVisitor, Builder<NucleotideFastaDataStore>{

		private final Map<String, NucleotideFastaRecord> fastaRecords = new LinkedHashMap<String, NucleotideFastaRecord>();
		
		private final DataStoreFilter filter;
		private final ReusableNucleotideFastaRecordVisitor currentVisitor = new ReusableNucleotideFastaRecordVisitor();
		public NucleotideFastaDataStoreBuilderVisitorImpl2(DataStoreFilter filter){
			this.filter = filter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			currentVisitor.initialize(id, optionalComment);
			return currentVisitor;
			
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public void halted() {
			//no-op			
		}
		@Override
		public NucleotideFastaDataStore build() {
			return DataStoreUtil.adapt(NucleotideFastaDataStore.class,fastaRecords);
		}
		/**
		 * {@code ReusableNucleotideFastaRecordVisitor}
		 * is a {@link FastaRecordVisitor} that can be "reset"
		 * and used multiple times.  This should cut down
		 * on object creation and garbage collection
		 * since we expect there could be hundreds of thousands
		 * or millions of records to visit.
		 * 
		 * Before each new record to visit, call {@link #initialize(String, String)}.
		 * @author dkatzel
		 *
		 */
		 private final class ReusableNucleotideFastaRecordVisitor implements FastaRecordVisitor{
			private String currentId;
			private String currentComment;
			private NucleotideSequenceBuilder builder;
			/**
			 * Default constructor needs to have it's data
			 * initialized.
			 */
			public ReusableNucleotideFastaRecordVisitor(){
				//need to explicitly declare default constructor
				//to add javadoc comment
			}
			/**
			 * Prepare this visitor to visit a new record.
			 * @param id the id of the record to be visited.
			 * @param optionalComment the optional comment of the record
			 * to be visited.
			 */
			public void initialize(String id, String optionalComment){
				this.currentId = id;
				this.currentComment = optionalComment;
				builder = new NucleotideSequenceBuilder();
			}
			@Override
			public void visitBodyLine(String line) {
				builder.append(line);
				
			}

			@Override
			public void visitEnd() {
				NucleotideFastaRecord record = new NucleotideFastaRecordBuilder(currentId,builder.build())
														.comment(currentComment)
														.build();
				fastaRecords.put(currentId, record);
				
			}
			@Override
			public void halted() {
				//no-op				
			}
			
		    	
	    }
	}
    
   
}
