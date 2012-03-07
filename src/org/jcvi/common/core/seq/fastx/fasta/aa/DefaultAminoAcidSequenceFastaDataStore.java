package org.jcvi.common.core.seq.fastx.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;

public final class DefaultAminoAcidSequenceFastaDataStore implements AminoAcidSequenceFastaDataStore{

	
	public static AminoAcidSequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		AminoAcidSequenceFastaDataStoreBuilderVisitor builder = createBuilder();
		FastaParser.parseFasta(fastaFile, builder);
		return builder.build();
	}
	
	public static AminoAcidSequenceFastaDataStoreBuilderVisitor createBuilder(){
		return new DefaultAminoAcidSequenceFastaDataStoreBuilder();
	}
	
	
	
	
	private static final class DefaultAminoAcidSequenceFastaDataStoreBuilder implements AminoAcidSequenceFastaDataStoreBuilderVisitor{
		private final Map<String, AminoAcidSequenceFastaRecord> fastaRecords = new LinkedHashMap<String, AminoAcidSequenceFastaRecord>();
		@Override
		public FastaDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore> addFastaRecord(
				AminoAcidSequenceFastaRecord fastaRecord) {
			fastaRecords.put(fastaRecord.getId(), fastaRecord);
			return this;
		}

		@Override
		public AminoAcidSequenceFastaDataStore build() {
			return new DefaultAminoAcidSequenceFastaDataStore(new SimpleDataStore<AminoAcidSequenceFastaRecord>(fastaRecords));
		}

		@Override
		public boolean visitDefline(String defline) {
			return true;
		}

		@Override
		public boolean visitBodyLine(String bodyLine) {
			return true;
		}

		@Override
		public boolean visitRecord(String id, String comment, String entireBody) {
			addFastaRecord(new DefaultAminoAcidSequenceFastaRecord(id, comment, entireBody.replaceAll("\\s+", "")));
			return true;
		}

		@Override
		public void visitLine(String line) {
			// no-op
			
		}

		@Override
		public void visitFile() {
			// no-op
			
		}

		@Override
		public void visitEndOfFile() {
			// no-op
			
		}
		
	}
	private final DataStore<AminoAcidSequenceFastaRecord> delegate;
	
	private DefaultAminoAcidSequenceFastaDataStore(DataStore<AminoAcidSequenceFastaRecord> delegate){
		this.delegate = delegate;
	}

	@Override
	public CloseableIterator<String> getIds() throws DataStoreException {
		return delegate.getIds();
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
	public int size() throws DataStoreException {
		return delegate.size();
	}

	@Override
	public boolean isClosed() throws DataStoreException {
		return delegate.isClosed();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
		
	}

	@Override
	public CloseableIterator<AminoAcidSequenceFastaRecord> iterator() {
		return delegate.iterator();
	}
	
	
	
}
