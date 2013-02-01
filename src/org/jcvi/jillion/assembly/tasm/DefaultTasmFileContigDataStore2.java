package org.jcvi.jillion.assembly.tasm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.tasm.DefaultTasmContig.Builder;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;

final class DefaultTasmFileContigDataStore2 {

	public static TasmContigDataStore create(File tasmFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter,fullLengthSequenceDataStore);
		TasmFileParser2.create(tasmFile).accept(visitor);
		return DataStoreUtil.adapt(TasmContigDataStore.class, visitor.contigs);
	}
	
	
	private static final class Visitor implements TasmFileVisitor2{
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
			return new AbstractTasmContigVisitor(contigId, fullLengthSequenceDataStore) {
				
				@Override
				protected void visitRecord(Builder builder) {
					contigs.put(contigId, builder.build());
					
				}
			};
		}

		@Override
		public void visitIncompleteEnd() {
			//no-op
			
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		
	}
}
