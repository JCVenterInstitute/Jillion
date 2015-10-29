package org.jcvi.jillion.fasta.nt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.fasta.DefaultFastaIndex;
import org.jcvi.jillion.internal.fasta.FastaIndex;
import org.jcvi.jillion.internal.fasta.FastaIndexRecord;
/**
 * {@link NucleotideFastaDataStore} implementation that uses
 * a FastaIndex to quickly seek to the appropriate part in
 * the fasta file to get a sequence or subsequence.
 * 
 * @author dkatzel
 *
 * @since 5.1
 */
class FaiNucleotideFastaFileDataStore implements NucleotideFastaDataStore{

	private final File fastaFile;
	private final FastaIndex index;
	
	private final NucleotideFastaDataStore delegate;

	
	public static FaiNucleotideFastaFileDataStore create(File fastaFile, File faiFile,  NucleotideFastaDataStore delegate) throws IOException{
		return new FaiNucleotideFastaFileDataStore(fastaFile, DefaultFastaIndex.parse(faiFile), delegate);
	}
	public FaiNucleotideFastaFileDataStore(File fastaFile, FastaIndex index, NucleotideFastaDataStore delegate) throws IOException {
		
		Objects.requireNonNull(delegate);
		Objects.requireNonNull(index);
		IOUtil.verifyIsReadable(fastaFile);
		
		this.fastaFile = fastaFile;		
		this.delegate = delegate;		
		this.index = index;
		
		
		
	}

	
	
	private void throwExceptionIfClosed(){
		if(isClosed()){
			throw new DataStoreClosedException("closed");
		}
	}
	@Override
	public NucleotideSequence getSequence(String id) throws DataStoreException {		
		return getSequence(id, (record)-> record.newInputStream(fastaFile));
	}

	private NucleotideSequence getSequence(String id, InputStreamFactory inputStreamFactory) throws DataStoreException {
		throwExceptionIfClosed();
		FastaIndexRecord record = index.getIndexFor(id);
		if(record ==null){
			return null;
		}
		try(InputStream in = inputStreamFactory.get(record);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, IOUtil.UTF_8))
		){
			
			NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder((int) record.getSeqLength());
			String line;
			while( (line = reader.readLine()) !=null){
				builder.append(line);
			}
			return builder.build();
		
		} catch (IOException e) {
			throw new DataStoreException("error seeking to record location specified by fai file", e);
		}
	}
	

	@Override
	public NucleotideSequence getSubSequence(String id, long startOffset) throws DataStoreException {
		return getSequence(id, (record)-> record.newInputStream(fastaFile, startOffset));
	}




	@Override
	public NucleotideSequence getSubSequence(String id, Range includeRange) throws DataStoreException {
		return getSequence(id, (record)-> record.newInputStream(fastaFile, includeRange));
	}




	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return delegate.idIterator();
	}

	@Override
	public NucleotideFastaRecord get(String id) throws DataStoreException {
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
	public StreamingIterator<NucleotideFastaRecord> iterator() throws DataStoreException {
		return delegate.iterator();
	}

	@Override
	public StreamingIterator<DataStoreEntry<NucleotideFastaRecord>> entryIterator() throws DataStoreException {
		return delegate.entryIterator();
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}
	
	/**
	 * Interface for Lambda expression
	 * that can throw IOException so lambdas 
	 * can be one liners and not have to wrap the 
	 * code in try-catch blocks.
	 * 
	 * @author dkatzel
	 *
	 */
	private interface InputStreamFactory{
		InputStream get(FastaIndexRecord record) throws IOException;
	}
	
}
