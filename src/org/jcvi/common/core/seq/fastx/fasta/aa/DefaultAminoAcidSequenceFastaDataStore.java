package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

public final class DefaultAminoAcidSequenceFastaDataStore implements AminoAcidSequenceFastaDataStore{
	
	private final DataStore<AminoAcidSequenceFastaRecord> delegate;
	
	
	public static AminoAcidSequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		AminoAcidSequenceFastaDataStoreBuilderVisitor builder = createBuilder();
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	
	public static AminoAcidSequenceFastaDataStoreBuilderVisitor createBuilder(){
		return new DefaultAminoAcidSequenceFastaDataStoreBuilder();
	}
	private DefaultAminoAcidSequenceFastaDataStore(DataStore<AminoAcidSequenceFastaRecord> delegate){
		this.delegate = delegate;
	}

	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return delegate.idIterator();
	}

	@Override
	public AminoAcidSequenceFastaRecord get(String id)
			throws DataStoreException {
		return delegate.get(id);
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		return delegate.contains(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		return delegate.getNumberOfRecords();
	}

	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
		
	}

	@Override
	public StreamingIterator<AminoAcidSequenceFastaRecord> iterator() throws DataStoreException {
		return delegate.iterator();
	}
	
	private static final class DefaultAminoAcidSequenceFastaDataStoreBuilder extends AbstractFastaVisitor implements AminoAcidSequenceFastaDataStoreBuilderVisitor{
		private final Map<String, AminoAcidSequenceFastaRecord> fastaRecords = new LinkedHashMap<String, AminoAcidSequenceFastaRecord>();
		@Override
		public FastaDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore> addFastaRecord(
				AminoAcidSequenceFastaRecord fastaRecord) {
			fastaRecords.put(fastaRecord.getId(), fastaRecord);
			return this;
		}

		@Override
		public AminoAcidSequenceFastaDataStore build() {
			return new DefaultAminoAcidSequenceFastaDataStore(MapDataStoreAdapter.adapt(fastaRecords));
		}


		@Override
		public boolean visitRecord(String id, String comment, String entireBody) {
			addFastaRecord(new DefaultAminoAcidSequenceFastaRecord(id, comment, entireBody.replaceAll("\\s+", "")));
			return true;
		}

		
	}
	
}
