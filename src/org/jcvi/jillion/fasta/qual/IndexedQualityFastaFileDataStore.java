package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaFileVisitor;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractIndexedFastaDataStoreBuilderVisitor;
/**
 * {@code IndexedQualityFastaFileDataStore} is an implementation of 
 * {@link QualitySequenceFastaDataStore} that only stores an index containing
 * file offsets to the various {@link FastaRecord}s contained
 * inside the fasta file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fasta record
 * must be seeked to and then re-parsed each time and the fasta file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedQualityFastaFileDataStore implements QualitySequenceFastaDataStore{

	private final Map<String,Range> index;
	private final File fastaFile;
	private volatile boolean isClosed;
	/**
	 * Creates a new {@link IndexedQualityFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedQualityFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedQualityFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static QualitySequenceFastaDataStore create(File fastaFile) throws FileNotFoundException{
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	
	/**
	 * Creates a new {@link IndexedQualityFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedQualityFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedQualityFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static QualitySequenceFastaDataStore create(File fastaFile, DataStoreFilter filter) throws FileNotFoundException{
		QualityFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile,filter);
		FastaFileParser.parse(fastaFile, builder);
		return builder.build();
	}
	/**
	 * Creates a new {@link QualityFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedQualityFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link QualityFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link QualityFastaDataStoreBuilderVisitor#addFastaRecord(QualitySequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedQualityFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link QualityFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static QualityFastaDataStoreBuilderVisitor createBuilder(File fastaFile){
		return createBuilder(fastaFile, DataStoreFilters.alwaysAccept());
	}
	
	/**
	 * Creates a new {@link QualityFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedQualityFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link QualityFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link QualityFastaDataStoreBuilderVisitor#addFastaRecord(QualitySequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedQualityFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link QualityFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static QualityFastaDataStoreBuilderVisitor createBuilder(File fastaFile, DataStoreFilter filter){
		return new IndexedQualityFastaDataStoreBuilderVisitor(fastaFile, filter);
	}
	
	private IndexedQualityFastaFileDataStore(Map<String,Range> index,File fastaFile){
		this.index = index;
		this.fastaFile = fastaFile;
	}
	
	private void throwExceptionIfClosed(){
		if(isClosed){
			throw new IllegalStateException("datastore is closed");
		}
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this,index.keySet().iterator());
	}

	@Override
	public QualitySequenceFastaRecord get(String id)
			throws DataStoreException {
		throwExceptionIfClosed();
		if(!index.containsKey(id)){
			return null;
		}
		InputStream in = null;
		try{
			Range range = index.get(id);
			in = IOUtil.createInputStreamFromFile(fastaFile, (int)range.getBegin(), (int)range.getLength());
			QualitySequenceFastaDataStore datastore = DefaultQualityFastaFileDataStore.create(in);
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
		return isClosed;
	}

	@Override
	public void close() throws IOException {
		isClosed =true;
		
	}

	@Override
	public StreamingIterator<QualitySequenceFastaRecord> iterator() {
		QualitySequenceFastaDataStoreIteratorImpl iter= new QualitySequenceFastaDataStoreIteratorImpl(fastaFile);
		iter.start();
		return DataStoreStreamingIterator.create(this,iter);
	}
	
	private static final class IndexedQualityFastaDataStoreBuilderVisitor 
	extends AbstractIndexedFastaDataStoreBuilderVisitor<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>
			implements	QualityFastaDataStoreBuilderVisitor{

			private IndexedQualityFastaDataStoreBuilderVisitor(File fastaFile, DataStoreFilter filter){
				super(fastaFile, filter);
			}
			@Override
			protected QualitySequenceFastaDataStore createDataStore(
				Map<String,Range> index, File fastaFile) {
				return new IndexedQualityFastaFileDataStore(index, fastaFile);
			}


	}
}
