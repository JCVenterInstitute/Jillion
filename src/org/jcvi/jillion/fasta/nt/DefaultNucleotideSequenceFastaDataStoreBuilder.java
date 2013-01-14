package org.jcvi.jillion.fasta.nt;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
/**
 * {@code DefaultNucleotideFastaDataStoreBuilder} is a {@link NucleotideSequenceFastaDataStoreBuilder}
 * that stores all {@link NucleotideSequenceFastaRecord} added to it via the {@link #addFastaRecord(NucleotideSequenceFastaRecord)}
 * in  a Map.  All fastas are stored in memory so if too many records are added, this object could
 * take up considerable memory and could cause an {@link OutOfMemoryError}.
 * @author dkatzel
 *
 */
final class DefaultNucleotideSequenceFastaDataStoreBuilder implements NucleotideSequenceFastaDataStoreBuilder{

	private final Map<String, NucleotideSequenceFastaRecord> map = new LinkedHashMap<String, NucleotideSequenceFastaRecord>();
	@Override
	public NucleotideSequenceFastaDataStore build() {
		return DataStoreUtil.adapt(NucleotideSequenceFastaDataStore.class,map);
	}

	@Override
	public DefaultNucleotideSequenceFastaDataStoreBuilder addFastaRecord(
			NucleotideSequenceFastaRecord fastaRecord) {
		if(fastaRecord ==null){
			throw new NullPointerException("fasta record can not be null");
		}
		map.put(fastaRecord.getId(), fastaRecord);
		return this;
	}

}
