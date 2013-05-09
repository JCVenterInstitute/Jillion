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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultTasmFileContigDataStore}
 * is a {@link TasmContigDataStore}
 * implementation that stores all {@link TasmContig}s
 * from a tasm file
 * in a {@link Map}.
 * @author dkatzel
 *
 */
final class DefaultTasmFileContigDataStore {

	public static TasmContigDataStore create(File tasmFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter,fullLengthSequenceDataStore);
		TasmFileParser.create(tasmFile).accept(visitor);
		return DataStoreUtil.adapt(TasmContigDataStore.class, visitor.contigs);
	}
	
	private DefaultTasmFileContigDataStore(){
		//can not instantiate.
	}
	
	private static final class Visitor implements TasmFileVisitor{
		private final DataStoreFilter filter;
		private final DataStore<Long> fullLengthSequenceDataStore;
		private final Map<String, TasmContig> contigs = new LinkedHashMap<String, TasmContig>();
		
		public Visitor(DataStoreFilter filter, DataStore<Long> fullLengthSequenceDataStore) {
			this.filter = filter;
			this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
		}

		@Override
		public TasmContigVisitor visitContig(
				TasmContigVisitorCallback callback, final String contigId) {
			if(!filter.accept(contigId)){
				return null;
			}
			return new AbstractTasmContigBuilderVisitor(contigId, fullLengthSequenceDataStore) {
				
				@Override
				protected void visitRecord(TasmContigBuilder builder) {
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
