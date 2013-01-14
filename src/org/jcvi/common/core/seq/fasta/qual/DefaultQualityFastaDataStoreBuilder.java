package org.jcvi.common.core.seq.fasta.qual;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultQualityFastaDataStoreBuilder} is a {@link QualitySequenceFastaDataStoreBuilder}
 * that stores all {@link QualitySequenceFastaRecord} added to it via the {@link #addFastaRecord(QualitySequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
public class DefaultQualityFastaDataStoreBuilder implements QualitySequenceFastaDataStoreBuilder{

	private final Map<String, QualitySequenceFastaRecord> map = new LinkedHashMap<String, QualitySequenceFastaRecord>();
	@Override
	public QualitySequenceFastaDataStore build() {
		return DataStoreUtil.adapt(QualitySequenceFastaDataStore.class,map);
	}

	@Override
	public QualitySequenceFastaDataStoreBuilder addFastaRecord(
			QualitySequenceFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}
}
