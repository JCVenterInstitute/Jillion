package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.impl.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.AbstractIndexedFastaDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code IndexedNucleotideFastaFileDataStore} is an implementation of 
 * {@link NucleotideSequenceFastaDataStore} that only stores an index containing
 * file offsets to the various {@link FastaRecord}s contained
 * inside the fasta file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fasta record
 * must be seeked to and then re-parsed each time and the fasta file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedNucleotideSequenceFastaFileDataStore implements NucleotideSequenceFastaDataStore{
	
	private final Map<String,Range> index;
	private final File fastaFile;
	private final DataStoreFilter filter;
	private volatile boolean closed;
	/**
	 * Creates a new {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideSequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		NucleotideFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile);
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	/**
	 * Creates a new {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @param filter the {@link DataStoreFilter} to use to filter the records from the fasta file.
	 * @return a new instance of {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		NucleotideFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile, filter);
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	/**
	 * Creates a new {@link NucleotideFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link NucleotideFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link NucleotideFastaDataStoreBuilderVisitor#addFastaRecord(DefaultNucleotideSequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link NucleotideFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaDataStoreBuilderVisitor createBuilder(File fastaFile) throws FileNotFoundException{
		if(fastaFile ==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new FileNotFoundException(fastaFile.getAbsolutePath());
		}
		return new IndexedNucleotideFastaDataStoreBuilderVisitor(fastaFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Creates a new {@link NucleotideFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link NucleotideFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link NucleotideFastaDataStoreBuilderVisitor#addFastaRecord(DefaultNucleotideSequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link NucleotideFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaDataStoreBuilderVisitor createBuilder(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		if(fastaFile ==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new FileNotFoundException(fastaFile.getAbsolutePath());
		}
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		return new IndexedNucleotideFastaDataStoreBuilderVisitor(fastaFile,filter);
	}
	
	
	
	private IndexedNucleotideSequenceFastaFileDataStore(Map<String,Range> index, File fastaFile, DataStoreFilter filter){
		this.index = index;
		this.fastaFile = fastaFile;
		this.filter = filter;
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this,index.keySet().iterator());
	}

	@Override
	public NucleotideSequenceFastaRecord get(String id)
			throws DataStoreException {
		throwExceptionIfClosed();
		if(!index.containsKey(id)){
			return null;
		}
		InputStream in = null;
		try{
			Range range = index.get(id);
			in = IOUtil.createInputStreamFromFile(fastaFile, (int)range.getBegin(), (int)range.getLength());
			NucleotideSequenceFastaDataStore datastore = DefaultNucleotideSequenceFastaFileDataStore.create(in);
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
	public StreamingIterator<NucleotideSequenceFastaRecord> iterator() throws DataStoreException {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this,LargeNucleotideSequenceFastaIterator.createNewIteratorFor(fastaFile,filter));
	}
	
	private static final class IndexedNucleotideFastaDataStoreBuilderVisitor
			extends
			AbstractIndexedFastaDataStoreBuilderVisitor<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>
			implements NucleotideFastaDataStoreBuilderVisitor {
		
		private IndexedNucleotideFastaDataStoreBuilderVisitor(File fastaFile, DataStoreFilter filter) {
			super(fastaFile, filter);
		}

		@Override
		protected NucleotideSequenceFastaDataStore createDataStore(
				Map<String,Range> index, File fastaFile) {
			return new IndexedNucleotideSequenceFastaFileDataStore(index, fastaFile, getFilter());
		}


}

}
