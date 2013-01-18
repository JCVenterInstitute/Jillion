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
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser2;
import org.jcvi.jillion.fasta.FastaFileVisitor2;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
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
final class DefaultNucleotideSequenceFastaFileDataStore{
	
	private DefaultNucleotideSequenceFastaFileDataStore(){
		//can not instantiate.
	}

	private static NucleotideFastaDataStoreBuilderVisitorImpl2 createBuilder(DataStoreFilter filter){
		return new NucleotideFastaDataStoreBuilderVisitorImpl2(filter);
	}
	
	public static NucleotideSequenceFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile,DataStoreFilters.alwaysAccept());
	}
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		NucleotideFastaDataStoreBuilderVisitorImpl2 builder = createBuilder(filter);
		new FastaFileParser2(fastaFile).accept(builder);
		return builder.build();
	}
	
	public static NucleotideSequenceFastaDataStore create(InputStream in) throws IOException{
		return create(in,DataStoreFilters.alwaysAccept());
	}
	public static NucleotideSequenceFastaDataStore create(InputStream in, DataStoreFilter filter) throws IOException{
		try{
			NucleotideFastaDataStoreBuilderVisitorImpl2 builder = createBuilder(filter);
			new FastaFileParser2(in).accept(builder);
			return builder.build();
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
    

    
    private static final class NucleotideFastaDataStoreBuilderVisitorImpl2 implements FastaFileVisitor2, Builder<NucleotideSequenceFastaDataStore>{

		private final Map<String, NucleotideSequenceFastaRecord> fastaRecords = new LinkedHashMap<String, NucleotideSequenceFastaRecord>();
		
		private final DataStoreFilter filter;
		
		public NucleotideFastaDataStoreBuilderVisitorImpl2(DataStoreFilter filter){
			this.filter = filter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractNucleotideFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						NucleotideSequenceFastaRecord fastaRecord) {
					fastaRecords.put(id, fastaRecord);
					
				}
				
			};
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public NucleotideSequenceFastaDataStore build() {
			return DataStoreUtil.adapt(NucleotideSequenceFastaDataStore.class,fastaRecords);
		}
		
	}
}
