package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.AbstractIndexedFastaDataStoreBuilderVisitor;
import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileVisitor;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code IndexedPositionFastaFileDataStore} is an implementation of 
 * {@link PositionFastaDataStore} that only stores an index containing
 * file offsets to the various {@link FastaRecord}s contained
 * inside the fasta file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fasta record
 * must be seeked to and then re-parsed each time and the fasta file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedPositionFastaFileDataStore implements PositionFastaDataStore{
	/**
	 * Creates a new {@link IndexedPositionFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedPositionFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedPositionFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static PositionFastaDataStore create(File fastaFile) throws FileNotFoundException{
		PositionFastaDataStoreBuilderVisitor builder = createBuilder(fastaFile);
		FastaParser.parseFasta(fastaFile, builder);
		return builder.build();
	}
	/**
	 * Creates a new {@link PositionFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedPositionFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link PositionFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link PositionFastaDataStoreBuilderVisitor#addFastaRecord(PositionSequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedPositionFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link PositionFastaDataStoreBuilderVisitor};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static PositionFastaDataStoreBuilderVisitor createBuilder(File fastaFile){
		return new IndexedPositionFastaDataStoreBuilderVisitor(fastaFile);
	}
	private final IndexedFileRange index;
	private final File fastaFile;
	
	private IndexedPositionFastaFileDataStore(IndexedFileRange index, File fastaFile){
		this.index = index;
		this.fastaFile = fastaFile;
	}
	@Override
	public CloseableIterator<String> idIterator() throws DataStoreException {
		return index.getIds();
	}

	@Override
	public PositionSequenceFastaRecord<Sequence<ShortSymbol>> get(String id)
			throws DataStoreException {
		if(!contains(id)){
			return null;
		}
		InputStream in = null;
		try{
			in = IOUtil.createInputStreamFromFile(fastaFile, index.getRangeFor(id));
			PositionFastaDataStore datastore = DefaultPositionFastaFileDataStore.create(in);
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
	public CloseableIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> iterator() {
		return LargePositionFastaRecordIterator.createNewIteratorFor(fastaFile);
	}
	
	private static final class IndexedPositionFastaDataStoreBuilderVisitor 
	extends AbstractIndexedFastaDataStoreBuilderVisitor<ShortSymbol, Sequence<ShortSymbol>, PositionSequenceFastaRecord<Sequence<ShortSymbol>>, PositionFastaDataStore>
			implements	PositionFastaDataStoreBuilderVisitor{

			@Override
		public <F extends PositionSequenceFastaRecord<Sequence<ShortSymbol>>> IndexedPositionFastaDataStoreBuilderVisitor addFastaRecord(
				F fastaRecord) {
				//don't know why complier is complaining about type
				//just cast for now the method will throw an exception anyway...
			return (IndexedPositionFastaDataStoreBuilderVisitor) super.addFastaRecord(fastaRecord);
		}
			private IndexedPositionFastaDataStoreBuilderVisitor(File fastaFile){
				super(fastaFile);
			}
			@Override
			protected PositionFastaDataStore createDataStore(
				IndexedFileRange index, File fastaFile) {
				return new IndexedPositionFastaFileDataStore(index, fastaFile);
			}


	}
}
