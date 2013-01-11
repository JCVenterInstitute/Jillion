package org.jcvi.jillion.core.internal.seq.fasta.aa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fasta.FastaRecord;
import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.seq.fasta.AbstractIndexedFastaDataStoreBuilderVisitor;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;

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
public final class IndexedAminoAcidSequenceFastaFileDataStore implements AminoAcidSequenceFastaDataStore{
	
	private final Map<String,Range> index;
	private final File fastaFile;
	private volatile boolean closed;
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
	public static AminoAcidSequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		AminoAcidSequenceFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile);
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
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
	public static AminoAcidSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		AminoAcidSequenceFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile,filter);
		FastaFileParser.parse(fastaFile, builder);
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
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static AminoAcidSequenceFastaDataStoreBuilderVisitor createBuilder(File fastaFile) throws FileNotFoundException{
		return createBuilder(fastaFile, DataStoreFilters.alwaysAccept());
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
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static AminoAcidSequenceFastaDataStoreBuilderVisitor createBuilder(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		if(fastaFile ==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new FileNotFoundException(fastaFile.getAbsolutePath());
		}
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		return new IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor(fastaFile, filter);
	}
	
	
	private IndexedAminoAcidSequenceFastaFileDataStore(Map<String,Range> index, File fastaFile){
		this.index = index;
		this.fastaFile = fastaFile;
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this,index.keySet().iterator());
	}

	@Override
	public AminoAcidSequenceFastaRecord get(String id)
			throws DataStoreException {
		throwExceptionIfClosed();
		if(!index.containsKey(id)){
			return null;
		}
		InputStream in = null;
		try{
			Range range = index.get(id);
			in = IOUtil.createInputStreamFromFile(fastaFile, (int)range.getBegin(), (int)range.getLength());
			AminoAcidSequenceFastaDataStoreBuilderVisitor builderVisitor = DefaultAminoAcidSequenceFastaDataStore.createBuilder();
			FastaFileParser.parse(in, builderVisitor);
			AminoAcidSequenceFastaDataStore datastore = builderVisitor.build();
			return datastore.get(id);
		} catch (IOException e) {
			throw new DataStoreException("error reading fasta file",e);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		throwExceptionIfClosed();
		return index.containsKey(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		throwExceptionIfClosed();
		return index.size();
	}

	@Override
	public boolean isClosed(){
		return closed;
	}

	@Override
	public void close() {
		closed=true;
		
	}

	private void throwExceptionIfClosed() throws DataStoreException{
		if(closed){
			throw new IllegalStateException("datastore is closed");
		}
	}
	@Override
	public StreamingIterator<AminoAcidSequenceFastaRecord> iterator() throws DataStoreException {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this,
				LargeAminoAcidSequenceFastaIterator.createNewIteratorFor(fastaFile));
	}
	
	private static final class IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor
			extends
			AbstractIndexedFastaDataStoreBuilderVisitor<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore>
			implements AminoAcidSequenceFastaDataStoreBuilderVisitor {

		private IndexedAminoAcidSequenceFastaDataStoreBuilderVisitor(File fastaFile, DataStoreFilter filter) {
			super(fastaFile, filter);
		}

		@Override
		protected AminoAcidSequenceFastaDataStore createDataStore(
				Map<String,Range> index, File fastaFile) {
			return new IndexedAminoAcidSequenceFastaFileDataStore(index, fastaFile);
		}


}

}