package org.jcvi.jillion.internal.fasta.qual;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.qual.AbstractQualityFastaRecordVisitor;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualitySequenceFastaRecord;

public class DefaultQualityFastaFileDataStoreBuilder implements FastaFileVisitor, Builder<QualitySequenceFastaDataStore>{

	private final Map<String, QualitySequenceFastaRecord> fastaRecords = new LinkedHashMap<String, QualitySequenceFastaRecord>();
	
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
		return new AbstractQualityFastaRecordVisitor(id,optionalComment){

			@Override
			protected void visitRecord(
					QualitySequenceFastaRecord fastaRecord) {
				fastaRecords.put(id, fastaRecord);
				
			}
			
		};
	}

	@Override
	public void visitEnd() {
		//no-op			
	}
	@Override
	public QualitySequenceFastaDataStore build() {
		return DataStoreUtil.adapt(QualitySequenceFastaDataStore.class,fastaRecords);
	}
	
}
