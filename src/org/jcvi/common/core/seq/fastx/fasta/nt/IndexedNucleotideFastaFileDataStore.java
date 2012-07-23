package org.jcvi.common.core.seq.fastx.fasta.nt;

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
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.IndexedFileRange;
import org.jcvi.common.core.util.iter.CloseableIterator;
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
public final class IndexedNucleotideFastaFileDataStore implements NucleotideSequenceFastaDataStore{
	
	private final IndexedFileRange index;
	private final File fastaFile;
	
	/**
	 * Creates a new {@link IndexedNucleotideFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedNucleotideFastaFileDataStore};
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
	 * Creates a new {@link NucleotideFastaDataStoreBuilderVisitor}
	 * instance that will build an {@link IndexedNucleotideFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link NucleotideFastaDataStoreBuilderVisitor}
	 * can only be used to parse a single fasta file (the one given) and does not support
	 * {@link NucleotideFastaDataStoreBuilderVisitor#addFastaRecord(NucleotideSequenceFastaRecord)}.
	 * This builder visitor can only build the datastore via the visitXXX methods in the {@link FastaFileVisitor}
	 * interface.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideFastaFileDataStore}
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
		return new IndexedNucleotideFastaDataStoreBuilderVisitor(fastaFile);
	}
	
	
	
	
	private IndexedNucleotideFastaFileDataStore(IndexedFileRange index, File fastaFile){
		this.index = index;
		this.fastaFile = fastaFile;
	}
	@Override
	public CloseableIterator<String> idIterator() throws DataStoreException {
		return index.getIds();
	}

	@Override
	public NucleotideSequenceFastaRecord get(String id)
			throws DataStoreException {
		if(!contains(id)){
			return null;
		}
		InputStream in = null;
		try{
			in = IOUtil.createInputStreamFromFile(fastaFile, index.getRangeFor(id));
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
	public CloseableIterator<NucleotideSequenceFastaRecord> iterator() {
		return LargeNucleotideSequenceFastaIterator.createNewIteratorFor(fastaFile);
	}
	
	private static final class IndexedNucleotideFastaDataStoreBuilderVisitor
			extends
			AbstractIndexedFastaDataStoreBuilderVisitor<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>
			implements NucleotideFastaDataStoreBuilderVisitor {

		private IndexedNucleotideFastaDataStoreBuilderVisitor(File fastaFile) {
			super(fastaFile);
		}

		@Override
		protected NucleotideSequenceFastaDataStore createDataStore(
				IndexedFileRange index, File fastaFile) {
			return new IndexedNucleotideFastaFileDataStore(index, fastaFile);
		}


}

}
