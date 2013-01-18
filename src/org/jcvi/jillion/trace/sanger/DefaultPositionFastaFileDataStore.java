package org.jcvi.jillion.trace.sanger;

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
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;

public final class DefaultPositionFastaFileDataStore {
	
	private DefaultPositionFastaFileDataStore(){
		//can not instantiate
	}
	public static PositionSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		DefaultQualityFastaFileDataStoreBuilder builder = new DefaultQualityFastaFileDataStoreBuilder(filter);
		new FastaFileParser(fastaFile).accept(builder);
    	return builder.build();
	}
	public static PositionSequenceFastaDataStore create(InputStream positionFastaInputStream, DataStoreFilter filter) throws IOException{
		DefaultQualityFastaFileDataStoreBuilder builder = new DefaultQualityFastaFileDataStoreBuilder(filter);
		new FastaFileParser(positionFastaInputStream).accept(builder);
    	return builder.build();
	}
	public static PositionSequenceFastaDataStore create(File positionFastaFile) throws IOException{
		return create(positionFastaFile, DataStoreFilters.alwaysAccept());
	}
	public static PositionSequenceFastaDataStore create(InputStream positionFastaInputStream) throws IOException{
		return create(positionFastaInputStream, DataStoreFilters.alwaysAccept());
	}

	
	private static class DefaultQualityFastaFileDataStoreBuilder implements FastaFileVisitor, Builder<PositionSequenceFastaDataStore>{

		private final Map<String, PositionSequenceFastaRecord> fastaRecords = new LinkedHashMap<String, PositionSequenceFastaRecord>();
		
		private final DataStoreFilter filter;
		
		public DefaultQualityFastaFileDataStoreBuilder(DataStoreFilter filter){
			this.filter = filter;
		}
		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				final String id, String optionalComment) {
			if(!filter.accept(id)){
				return null;
			}
			return new AbstractPositionSequenceFastaRecordVisitor(id,optionalComment){

				@Override
				protected void visitRecord(
						PositionSequenceFastaRecord fastaRecord) {
					fastaRecords.put(id, fastaRecord);
					
				}
				
			};
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public PositionSequenceFastaDataStore build() {
			return DataStoreUtil.adapt(PositionSequenceFastaDataStore.class,fastaRecords);
		}
		
	}
}
