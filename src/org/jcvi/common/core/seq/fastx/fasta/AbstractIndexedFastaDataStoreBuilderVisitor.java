package org.jcvi.common.core.seq.fastx.fasta;

import java.io.File;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
/**
 * {@code AbstractIndexedFastaDataStoreBuilderVisitor} is an
 * abstract implementation of {@link FastaFileDataStoreBuilderVisitor}
 * that indexes the file offsets of all the fasta records
 * in the input fasta file.  This allows creation of {@link FastaDataStore}s
 * with many fasta files to have random access to its members without
 * taking up a large memory footprint.  Implementations of this 
 * class only need to implement {@link #createDataStore(IndexedFileRange, File)}
 * and add any additional type specific interfaces to its signature.
 * @author dkatzel
 *
 * @param <S> the type of {@link Symbol} in the sequence of the fasta.
 * @param <T> the {@link Sequence} of the fasta.
 * @param <F> the {@link FastaRecord} type.
 * @param <D> the {@link DataStore} type to build.
 */
public abstract class AbstractIndexedFastaDataStoreBuilderVisitor<S extends Symbol, T extends Sequence<S>, F extends FastaRecord<S, T>, D extends DataStore<F>> implements FastaFileDataStoreBuilderVisitor<S,T,F,D>{
	private final IndexedFileRange index = new DefaultIndexedFileRange();
	private long currentStartOffset=0;
	private long currentOffset=0;
	private long currentLineLength=0;
	private final File fastaFile;
	private String lastId=null;
	private boolean doneReadingFile=false;
	private boolean built=false;
	
	protected AbstractIndexedFastaDataStoreBuilderVisitor(File fastaFile){
		this.fastaFile = fastaFile;
	}
	@Override
	public FastaFileDataStoreBuilderVisitor<S,T,F,D> addFastaRecord(
			F fastaRecord) {
		throw new UnsupportedOperationException("can not add fastas manually to visitor; only through visit methods");
	}
	/**
	 * Create a new Indexed Fasta DataStore using the IndexedFileRange and fasta file given.
	 * This method will only be called once from the {@link #build()}
	 * method after all the fasta records from the file have been indexed.
	 * @param index
	 * @param fastaFile
	 * @return a new datastore, never null.
	 */
	protected abstract D createDataStore(IndexedFileRange index, File fastaFile);
	@Override
	public synchronized D build() {
		if(built){
			throw new IllegalStateException("can only build once");
		}
		built=true;
		return createDataStore(index, fastaFile);
	}

	@Override
	public synchronized boolean visitDefline(String defline) {
		throwErrorIfDone();
		return true;
	}

	@Override
	public synchronized boolean visitBodyLine(String bodyLine) {
		throwErrorIfDone();
		return true;
	}

	@Override
	public synchronized boolean visitRecord(String id, String comment, String entireBody) {
		throwErrorIfDone();
		lastId = id;
		long endOfRecord = currentOffset - currentLineLength-1;
		index.put(id, Range.buildRange(currentStartOffset, endOfRecord));
		currentStartOffset = endOfRecord+1;
		return true;
	}

	@Override
	public synchronized void visitLine(String line) {
		throwErrorIfDone();
		currentLineLength = line.length();
		currentOffset += currentLineLength;
	}

	@Override
	public synchronized void visitFile() {
		throwErrorIfDone();
		
	}
	private synchronized void throwErrorIfDone(){
		if(doneReadingFile){
			throw new IllegalStateException("can only parse 1 fasta file");
		}
	}
	@Override
	public synchronized void visitEndOfFile() {
		doneReadingFile=true;
		if(lastId !=null){
			//because we won't know when we get to the end 
			//of the file until our last record will 
			//be missing the last line (since we chop it off
			//assuming it's the start of the next record
			//this code will add that line back on.
			Range updatedRange = index.getRangeFor(lastId).grow(0, currentLineLength-1);
			index.put(lastId, updatedRange);
		}
		
	}
	
	@Override
	public boolean supportsAddFastaRecord() {
		return false;
	} 
	
}
