package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.AbstractIndexedFastaDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.CloseableIterator;
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
public final class IndexedQualityFastaFileDataStore implements QualitySequenceFastaDataStore{

	private final IndexedFileRange index;
	private final File fastaFile;
	
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
		QualityFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile);
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
		return new IndexedQualityFastaDataStoreBuilderVisitor(fastaFile);
	}
	
	private IndexedQualityFastaFileDataStore(IndexedFileRange index, File fastaFile){
		this.index = index;
		this.fastaFile = fastaFile;
	}
	@Override
	public CloseableIterator<String> idIterator() throws DataStoreException {
		return index.getIds();
	}

	@Override
	public QualitySequenceFastaRecord get(String id)
			throws DataStoreException {
		if(!contains(id)){
			return null;
		}
		InputStream in = null;
		try{
			in = IOUtil.createInputStreamFromFile(fastaFile, index.getRangeFor(id));
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
		return index.contains(id);
	}

	@Override
	public long getNumberOfRecords() throws DataStoreException {
		return index.size();
	}

	@Override
	public boolean isClosed() throws DataStoreException {
		return index.isClosed();
	}

	@Override
	public void close() throws IOException {
		index.close();
		
	}

	@Override
	public CloseableIterator<QualitySequenceFastaRecord> iterator() {
		return LargeQualityFastaIterator.createNewIteratorFor(fastaFile);
	}
	
	private static final class IndexedQualityFastaDataStoreBuilderVisitor 
	extends AbstractIndexedFastaDataStoreBuilderVisitor<PhredQuality, QualitySequence, QualitySequenceFastaRecord, QualitySequenceFastaDataStore>
			implements	QualityFastaDataStoreBuilderVisitor{

			private IndexedQualityFastaDataStoreBuilderVisitor(File fastaFile){
				super(fastaFile);
			}
			@Override
			protected QualitySequenceFastaDataStore createDataStore(
				IndexedFileRange index, File fastaFile) {
				return new IndexedQualityFastaFileDataStore(index, fastaFile);
			}


	}
}
