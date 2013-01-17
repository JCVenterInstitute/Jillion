package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser2;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaFileVisitor2;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.Memento;
import org.jcvi.jillion.fasta.aa.AbstractFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

/**
 * {@code IndexedAminoAcidSequenceFastaFileDataStore} is an implementation of 
 * {@link AminoAcidSequenceFastaDataStore} that only stores an index containing
 * file offsets to the various {@link FastaRecord}s contained
 * inside the fasta file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fasta record
 * must be seeked to and then re-parsed each time and the fasta file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedAminoAcidSequenceFastaFileDataStore{

	/**
	 * Creates a new {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedAminoAcidSequenceFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static AminoAcidSequenceFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	
	/**
	 * Creates a new {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * for.
	 * @param filter the {@link DataStoreFilter} instance used to filter out the fasta records;
	 * can not be null.
	 * @return a new instance of {@link IndexedAminoAcidSequenceFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static AminoAcidSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2 builder = createBuilder(fastaFile,filter);
		builder.initialize();
		return builder.build();
	}
	
	
	/**
	 * Creates a new {@link AminoAcidSequenceFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link AminoAcidSequenceFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link AminoAcidSequenceFastaDataStoreBuilderVisitor#addFastaRecord(AminoAcidSequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedAminoAcidSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link AminoAcidSequenceFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws IOException 
	 * @throws NullPointerException if the input fasta file is null.
	 */
	private static IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2 createBuilder(File fastaFile, DataStoreFilter filter) throws IOException{
		if(fastaFile ==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new FileNotFoundException(fastaFile.getAbsolutePath());
		}
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		return new IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2(fastaFile, filter);
	}
	
	
	
	
	private static final class IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2 implements FastaFileVisitor2, Builder<AminoAcidSequenceFastaDataStore> {
	
		private final DataStoreFilter filter;
		private final FastaFileParser2 parser;
		private final File fastaFile;
		
		private final Map<String, FastaVisitorCallback.Memento> mementos = new LinkedHashMap<String, FastaVisitorCallback.Memento>();
		private IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor2(File fastaFile, DataStoreFilter filter) throws IOException {
			this.fastaFile = fastaFile;
			this.filter = filter;
			this.parser = new FastaFileParser2(fastaFile);

		}

		public void initialize() throws IOException {
			parser.accept(this);
			
		}

		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(filter.accept(id)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("must be able to create memento");
				}
				mementos.put(id, callback.createMemento());
			}
			//always skip records since we don't care about the details of any records
			//during the initial parse
			return null;
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}

		@Override
		public AminoAcidSequenceFastaDataStore build() {
			return new IndexedAminoAcidSequenceFastaFileDataStore2(fastaFile,parser, mementos);
		}
	
	}
	
	public static final class IndexedAminoAcidSequenceFastaFileDataStore2 implements AminoAcidSequenceFastaDataStore {
		private volatile boolean closed =false;
		private final File fastaFile;
		private final FastaFileParser2 parser;
		private final Map<String, FastaVisitorCallback.Memento> mementos;
		
		
		public IndexedAminoAcidSequenceFastaFileDataStore2(File fastaFile,
				FastaFileParser2 parser, Map<String, Memento> mementos) {
			this.fastaFile = fastaFile;
			this.parser = parser;
			this.mementos = mementos;
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this,mementos.keySet().iterator());
		}

		@Override
		public AminoAcidSequenceFastaRecord get(String id)
				throws DataStoreException {
			throwExceptionIfClosed();
			if(!mementos.containsKey(id)){
				return null;
			}
			SingleRecordVisitor visitor = new SingleRecordVisitor();
			try {
				parser.accept(mementos.get(id), visitor);
				return visitor.fastaRecord;
			} catch (IOException e) {
				throw new DataStoreException("error reading fasta file",e);
			}
		}

		@Override
		public StreamingIterator<AminoAcidSequenceFastaRecord> iterator() throws DataStoreException {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this,
					LargeAminoAcidSequenceFastaIterator.createNewIteratorFor(fastaFile));
		}
		private void throwExceptionIfClosed() throws DataStoreException{
			if(closed){
				throw new IllegalStateException("datastore is closed");
			}
		}
		public boolean contains(String id) throws DataStoreException {
			throwExceptionIfClosed();
			return mementos.containsKey(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			throwExceptionIfClosed();
			return mementos.size();
		}

		@Override
		public boolean isClosed(){
			return closed;
		}

		@Override
		public void close() {
			closed=true;
			
		}

	}

	private static class SingleRecordVisitor implements FastaFileVisitor2{
		private AminoAcidSequenceFastaRecord fastaRecord=null;
		@Override
		public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(fastaRecord !=null){
				callback.stopParsing();
				return null;
			}
			return new AbstractFastaRecordVisitor(id, optionalComment) {
				
				@Override
				protected void visitRecord(AminoAcidSequenceFastaRecord fastaRecord) {
					SingleRecordVisitor.this.fastaRecord = fastaRecord;
					
				}
			};
		}

		@Override
		public void visitEnd() {
			// TODO Auto-generated method stub
			
		}
		
	}
}

