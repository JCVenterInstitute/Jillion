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
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultTigrContigFileDataStore} is
 * a {@link TigrContigDataStore} implementation
 * that stores all {@link TigrContig}s in memory.
 * @author dkatzel
 *
 */
final class DefaultTigrContigFileDataStore {

	public static TigrContigDataStore create(File contigFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		DataStoreBuilder visitor = new DataStoreBuilder(fullLengthSequenceDataStore, filter);
		TigrContigFileParser.create(contigFile).accept(visitor);
		return DataStoreUtil.adapt(TigrContigDataStore.class, visitor.contigs);
	}
	
	private DefaultTigrContigFileDataStore(){
		//can not instantiate
	}
	
	private static final class DataStoreBuilder implements TigrContigFileVisitor{

		private final Map<String, TigrContig> contigs = new LinkedHashMap<String, TigrContig>();
		private final DataStoreFilter filter;
		private final DataStore<Long> fullLengthSequenceDataStore;
		
		public DataStoreBuilder(DataStore<Long> fullLengthSequenceDataStore,DataStoreFilter filter) {
			this.filter = filter;
			this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
		}

		@Override
		public TigrContigVisitor visitContig(TigrContigVisitorCallback callback,final String contigId) {
			if(!filter.accept(contigId)){
				return null;
			}
			return new AbstractTigrContigBuilderVisitor(contigId,fullLengthSequenceDataStore) {
				
				@Override
				protected void visitContig(TigrContigBuilder builder) {
					contigs.put(contigId, builder.build());
					
				}
			};
		}

		@Override
		public void halted() {
			//no-op
			
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		
	}
}
