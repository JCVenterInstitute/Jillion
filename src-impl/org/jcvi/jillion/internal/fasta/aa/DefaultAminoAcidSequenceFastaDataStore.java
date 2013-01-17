package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.AbstractFastaVisitor;
import org.jcvi.jillion.fasta.FastaDataStoreBuilder;
import org.jcvi.jillion.fasta.FastaFileParser2;
import org.jcvi.jillion.fasta.FastaFileVisitor2;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.aa.AbstractFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecordBuilder;

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
		FastaFileParser2 parser = new FastaFileParser2(fastaFile);
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
	private static final class DefaultAminoAcidSequenceFastaDataStoreBuilder2 implements FastaFileVisitor2, Builder<AminoAcidSequenceFastaDataStore>{

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
			return new AbstractFastaRecordVisitor(id,optionalComment){

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
	
	private static final class DefaultAminoAcidSequenceFastaDataStoreBuilder extends AbstractFastaVisitor implements AminoAcidSequenceFastaDataStoreBuilderVisitor{
		private final Map<String, AminoAcidSequenceFastaRecord> fastaRecords = new LinkedHashMap<String, AminoAcidSequenceFastaRecord>();
		
		private final DataStoreFilter filter;
		
		public DefaultAminoAcidSequenceFastaDataStoreBuilder(DataStoreFilter filter){
			this.filter = filter;
		}
		
		
		@Override
		public FastaDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore> addFastaRecord(
				AminoAcidSequenceFastaRecord fastaRecord) {
			if(filter.accept(fastaRecord.getId())){
				fastaRecords.put(fastaRecord.getId(), fastaRecord);
			}
			return this;
		}

		@Override
		public AminoAcidSequenceFastaDataStore build() {
			return DataStoreUtil.adapt(AminoAcidSequenceFastaDataStore.class,fastaRecords);
		}


		@Override
		public boolean visitRecord(String id, String comment, String entireBody) {
			addFastaRecord(new AminoAcidSequenceFastaRecordBuilder(id, entireBody)
								.comment(comment)
								.build());
			return true;
		}

		
	}
	
}
