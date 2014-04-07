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
package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractProteinFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.ProteinFastaDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;

public final class DefaultProteinFastaDataStore{
	
	private DefaultProteinFastaDataStore(){		
		//can not instantiate
	}
	public static ProteinFastaDataStore create(File fastaFile) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder();
		return parseFile(fastaFile, builder);
	}
	
	public static ProteinFastaDataStore create(InputStream in) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder();
		return parseFile(in, builder);
	}
	public static ProteinFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder(filter);
		return parseFile(fastaFile, builder);
	}
	public static ProteinFastaDataStore create(FastaParser parser, DataStoreFilter filter) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder(filter);
		return create(parser, builder);
	}
	public static ProteinFastaDataStore create(FastaParser parser) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder();
		return create(parser, builder);
	}
	public static ProteinFastaDataStore create(InputStream in, DataStoreFilter filter) throws IOException{
		DefaultProteinFastaDataStoreBuilder builder = createBuilder(filter);
		return parseFile(in, builder);
	}
	private static ProteinFastaDataStore parseFile(InputStream in, DefaultProteinFastaDataStoreBuilder visitor) throws IOException{
		FastaParser parser = FastaFileParser.create(in);
		return create(parser, visitor);
	}
	private static ProteinFastaDataStore create(FastaParser parser,
			DefaultProteinFastaDataStoreBuilder builder) throws IOException {
		parser.parse(builder);
		return builder.build();
	}
	private static ProteinFastaDataStore parseFile(File fastaFile, DefaultProteinFastaDataStoreBuilder visitor) throws IOException{
		FastaParser parser = FastaFileParser.create(fastaFile);
		return create(parser, visitor);
	}
	private static DefaultProteinFastaDataStoreBuilder createBuilder(){
		return createBuilder(DataStoreFilters.alwaysAccept());
	}
	private static DefaultProteinFastaDataStoreBuilder createBuilder(DataStoreFilter filter){
		//return new DefaultAminoAcidSequenceFastaDataStoreBuilder(filter);
		return new DefaultProteinFastaDataStoreBuilder(filter);
	}
	private static final class DefaultProteinFastaDataStoreBuilder implements FastaVisitor, Builder<ProteinFastaDataStore>{

		private final Map<String, ProteinFastaRecord> fastaRecords = new LinkedHashMap<String, ProteinFastaRecord>();
		
		private final DataStoreFilter filter;
		
		public DefaultProteinFastaDataStoreBuilder(DataStoreFilter filter){
			this.filter = filter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractProteinFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						ProteinFastaRecord fastaRecord) {
					fastaRecords.put(id, fastaRecord);
					
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
		@Override
		public ProteinFastaDataStore build() {
			return DataStoreUtil.adapt(ProteinFastaDataStore.class,fastaRecords);
		}
		
	}
	
}
