package org.jcvi.common.core.seq.fastx.fasta.aa.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.aa.AminoAcidSequenceFastaRecordBuilder;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public final class DefaultAminoAcidSequenceFastaDataStore{
	
	private DefaultAminoAcidSequenceFastaDataStore(){		
		//can not instantiate
	}
	public static AminoAcidSequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		AminoAcidSequenceFastaDataStoreBuilderVisitor builder = createBuilder();
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	public static AminoAcidSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		AminoAcidSequenceFastaDataStoreBuilderVisitor builder = createBuilder(filter);
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	public static AminoAcidSequenceFastaDataStoreBuilderVisitor createBuilder(){
		return createBuilder(DataStoreFilters.alwaysAccept());
	}
	public static AminoAcidSequenceFastaDataStoreBuilderVisitor createBuilder(DataStoreFilter filter){
		return new DefaultAminoAcidSequenceFastaDataStoreBuilder(filter);
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
			final boolean accept;
			if(filter instanceof FastXFilter){
				accept =((FastXFilter)filter).accept(fastaRecord.getId(), fastaRecord.getComment());
			}else{
				accept = filter.accept(fastaRecord.getId());
			}
			if(accept){
				fastaRecords.put(fastaRecord.getId(), fastaRecord);
			}
			return this;
		}

		@Override
		public AminoAcidSequenceFastaDataStore build() {
			return MapDataStoreAdapter.adapt(AminoAcidSequenceFastaDataStore.class,fastaRecords);
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
