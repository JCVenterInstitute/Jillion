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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractAminoAcidFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;

public final class DefaultAminoAcidSequenceFastaDataStore{
	
	private DefaultAminoAcidSequenceFastaDataStore(){		
		//can not instantiate
	}
	public static AminoAcidSequenceFastaDataStore create(File fastaFile) throws IOException{
		DefaultAminoAcidSequenceFastaDataStoreBuilder2 builder = createBuilder();
		return parseFile(fastaFile, builder);
	}
	public static AminoAcidSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		DefaultAminoAcidSequenceFastaDataStoreBuilder2 builder = createBuilder(filter);
		return parseFile(fastaFile, builder);
	}
	
	private static AminoAcidSequenceFastaDataStore parseFile(File fastaFile, DefaultAminoAcidSequenceFastaDataStoreBuilder2 visitor) throws IOException{
		FastaFileParser parser = FastaFileParser.create(fastaFile);
		parser.accept(visitor);
		return visitor.build();
	}
	private static DefaultAminoAcidSequenceFastaDataStoreBuilder2 createBuilder(){
		return createBuilder(DataStoreFilters.alwaysAccept());
	}
	private static DefaultAminoAcidSequenceFastaDataStoreBuilder2 createBuilder(DataStoreFilter filter){
		//return new DefaultAminoAcidSequenceFastaDataStoreBuilder(filter);
		return new DefaultAminoAcidSequenceFastaDataStoreBuilder2(filter);
	}
	private static final class DefaultAminoAcidSequenceFastaDataStoreBuilder2 implements FastaFileVisitor, Builder<AminoAcidSequenceFastaDataStore>{

		private final Map<String, AminoAcidSequenceFastaRecord> fastaRecords = new LinkedHashMap<String, AminoAcidSequenceFastaRecord>();
		
		private final DataStoreFilter filter;
		
		public DefaultAminoAcidSequenceFastaDataStoreBuilder2(DataStoreFilter filter){
			this.filter = filter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractAminoAcidFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						AminoAcidSequenceFastaRecord fastaRecord) {
					fastaRecords.put(id, fastaRecord);
					
				}
				
			};
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public AminoAcidSequenceFastaDataStore build() {
			return DataStoreUtil.adapt(AminoAcidSequenceFastaDataStore.class,fastaRecords);
		}
		
	}
	
}
