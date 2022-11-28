/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder.DecodingOptions;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
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
final class IndexedNucleotideSequenceFastaFileDataStore implements NucleotideFastaFileDataStore{
	
	private volatile boolean closed =false;
	private final FastaParser parser;
	private final Predicate<String> filter;
	private final Predicate<NucleotideFastaRecord> recordFilter;
	private final DecodingOptions decodingOptions;
	private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos;
	
	private final File fastaFile;
	
	private IndexedNucleotideSequenceFastaFileDataStore(FastaParser parser, Predicate<String> filter,
			Predicate<NucleotideFastaRecord> recordFilter, DecodingOptions decodingOptions,
			Map<String, FastaVisitorMemento> mementos) {
		this.parser = parser;
		this.mementos = mementos;
		this.filter = filter;
		this.recordFilter = recordFilter;
		this.decodingOptions = decodingOptions;
		File tmpFile = null;
                if( parser instanceof FastaFileParser){
                    Optional<File> optFile =((FastaFileParser)parser).getFile();
                    
                    if(optFile.isPresent()){
                        tmpFile = optFile.get();
                    }
                }
                
                fastaFile = tmpFile;
	}
	
	

	@Override
    public Optional<File> getFile() {
        return Optional.ofNullable(fastaFile);
    }



    @Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this,mementos.keySet().iterator());
	}

	@Override
	public NucleotideFastaRecord get(String id)
			throws DataStoreException {
		throwExceptionIfClosed();
		if(!mementos.containsKey(id)){
			return null;
		}
		SingleRecordVisitor visitor = new SingleRecordVisitor(this.decodingOptions);
		try {
			parser.parse(visitor, mementos.get(id));
			return visitor.fastaRecord;
		} catch (IOException e) {
			throw new DataStoreException("error reading fasta file",e);
		}
	}

	@Override
	public StreamingIterator<NucleotideFastaRecord> iterator() throws DataStoreException {
		throwExceptionIfClosed();
		try {
			return DataStoreStreamingIterator.create(this,
					LargeNucleotideSequenceFastaIterator.createNewIteratorFor(parser,filter, recordFilter));
		} catch (IOException e) {
			throw new DataStoreException("error iterating over fasta file", e);
		}
	}
	
	@Override
	public StreamingIterator<DataStoreEntry<NucleotideFastaRecord>> entryIterator()
			throws DataStoreException {
		throwExceptionIfClosed();
		StreamingIterator<DataStoreEntry<NucleotideFastaRecord>> entryIter;
		try {
			entryIter = new StreamingIterator<DataStoreEntry<NucleotideFastaRecord>>(){
				StreamingIterator<NucleotideFastaRecord> iter = LargeNucleotideSequenceFastaIterator.createNewIteratorFor(parser,filter, recordFilter);

				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}

				@Override
				public void close() {
					iter.close();
				}

				@Override
				public DataStoreEntry<NucleotideFastaRecord> next() {
					NucleotideFastaRecord next = iter.next();
					return new DataStoreEntry<NucleotideFastaRecord>(next.getId(), next);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			    
			};
		
		
			return DataStoreStreamingIterator.create(this, entryIter);
		} catch (IOException e) {
			throw new DataStoreException("error iterating over fasta file", e);
		}
	
	}
	private void throwExceptionIfClosed() throws DataStoreException{
		if(closed){
			throw new DataStoreClosedException("datastore is closed");
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
	/**
	 * Creates a new {@link NucleotideFastaDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link NucleotideFastaDataStore}
	 * for.
	 * @return a new instance of {@link NucleotideFastaDataStore};
	 * never null.
	 * @throws IOException  if there is a problem parsing the fasta file.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaFileDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept(), null);
	}
	/**
	 * Creates a new {@link NucleotideFastaDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link NucleotideFastaDataStore}
	 * for.
	 * @param filter the {@link Predicate} to use to filter the records from the fasta file.
	 * @return a new instance of {@link NucleotideFastaDataStore};
	 * never null.
	 * @throws IOException  if there is a problem parsing the fasta file.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaFileDataStore create(File fastaFile, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter) throws IOException{
		return create(fastaFile, filter, recordFilter, null);
	}
	/**
	 * Creates a new {@link NucleotideFastaDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link NucleotideFastaDataStore}
	 * for.
	 * @param filter the {@link Predicate} to use to filter the records from the fasta file.
	 * @return a new instance of {@link NucleotideFastaDataStore};
	 * never null.
	 * @throws IOException  if there is a problem parsing the fasta file.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaFileDataStore create(File fastaFile, Predicate<String> filter, 
			Predicate<NucleotideFastaRecord> recordFilter, DecodingOptions decodingOptions) throws IOException{
		BuilderVisitor builder = createBuilder(fastaFile, filter, recordFilter, decodingOptions);
		builder.initialize();
		return builder.build();
	}
	/**
	 * Creates a new {@link NucleotideFastaDataStore}
	 * instance using the given fastaFile.
	 * @param parser the {@link FastaParser} to use to create an {@link NucleotideFastaDataStore};
	 * can not be null.
	 * @return a new instance of {@link NucleotideFastaDataStore};
	 * never null.
	 * @throws IOException if there is a problem parsing the fasta data.
	 * @throws NullPointerException if the parser is null.
	 */
	public static NucleotideFastaFileDataStore create(FastaParser parser) throws IOException{
		return create(parser, DataStoreFilters.alwaysAccept(), null, null);
	}
	/**
	 * Creates a new {@link NucleotideFastaDataStore}
	 * instance using the given fastaFile.
	 * @param parser the {@link FastaParser} to use to create an {@link NucleotideFastaDataStore};
	 * can not be null.
	 * @param filter the {@link Predicate} to use to filter the records from the fasta file.
	 * @return a new instance of {@link NucleotideFastaDataStore};
	 * never null.
	 * @throws IOException if there is a problem parsing the fasta data.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static NucleotideFastaFileDataStore create(FastaParser parser, Predicate<String> filter, 
			Predicate<NucleotideFastaRecord> recordFilter,
			DecodingOptions decodingOptions) throws IOException{
		BuilderVisitor builder = createBuilder(parser, filter, recordFilter, decodingOptions);
		builder.initialize();
		return builder.build();
	}
	

	/**
	 * Creates a new {@link BuilderVisitor}
	 * instance that will build an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link BuilderVisitor}
	 * can only be used to parse a single fasta file (the one given).  
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @param filter an instance of {@link DataStoreFilter} to filter out records from the fasta file;
	 * can not be null.
	 * @return a new instance of {@link BuilderVisitor};
	 * never null.
	 * @throws IOException if the given fasta file does not exist
	 * or there is a problem parsing the fasta file.
	 * @throws NullPointerException if the input fasta file or filter are null.
	 */
	private static BuilderVisitor createBuilder(File fastaFile, Predicate<String> filter, 
			Predicate<NucleotideFastaRecord> recordFilter, DecodingOptions decodingOptions) throws IOException{
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		
		return new BuilderVisitor(fastaFile,filter, recordFilter, decodingOptions);
	}
	/**
	 * Creates a new {@link BuilderVisitor}
	 * instance that will build an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * using the given fastaFile.  This implementation of {@link BuilderVisitor}
	 * can only be used to parse a single fasta file (the one given).  
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @param filter an instance of {@link Predicate} to filter out records from the fasta file;
	 * can not be null.
	 * @return a new instance of {@link BuilderVisitor};
	 * never null.
	 * @throws IOException if the given fasta file does not exist.
	 * @throws NullPointerException if the input fasta file or filter are null.
	 */
	private static BuilderVisitor createBuilder(FastaParser parser, Predicate<String> filter,
			Predicate<NucleotideFastaRecord> recordFilter,
			DecodingOptions decodingOptions) throws IOException{
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		if(parser ==null){
			throw new NullPointerException("parser can not be null");
		}
		return new BuilderVisitor(parser,filter, recordFilter, decodingOptions);
	}
	
	
	

	
	private static final class BuilderVisitor implements FastaVisitor, Builder<NucleotideFastaFileDataStore> {
		
		private final Predicate<String> filter;
		private final Predicate<NucleotideFastaRecord> recordFilter;
		private final FastaParser parser;
		private final DecodingOptions decodingOptions;
		
		private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos = new LinkedHashMap<String, FastaVisitorCallback.FastaVisitorMemento>();
		
		public BuilderVisitor(File fastaFile, Predicate<String> filter, Predicate<NucleotideFastaRecord> recordFilter, DecodingOptions decodingOptions) throws IOException {
			this(FastaFileParser.create(fastaFile), filter, recordFilter, decodingOptions);

		}
		
		public BuilderVisitor(FastaParser parser, Predicate<String> filter, 
				Predicate<NucleotideFastaRecord> recordFilter, 
				DecodingOptions decodingOptions){
			this.filter = filter;
			this.parser =parser;
			this.decodingOptions = decodingOptions;
			this.recordFilter = recordFilter;
		}

		public void initialize() throws IOException {
			parser.parse(this);
			
		}

		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(filter.test(id)){
			    if(!callback.canCreateMemento()){
                                throw new IllegalStateException("must be able to create memento");
			    }
			    //get the memento here before we
			    //parse anymore so memento is at the beginning of record
			    FastaVisitorMemento memento = callback.createMemento();
			    if(recordFilter ==null){
			        //no more tests so we can include it
			        mementos.put(id, memento);
			        return null;
			    }
			    //need to parse the whole record to see if we should filter
			    return new AbstractNucleotideFastaRecordVisitor(id, optionalComment, decodingOptions, true) {
                                
                                @Override
                                protected void visitRecord(NucleotideFastaRecord fastaRecord) {
                                   if(recordFilter.test(fastaRecord)){
                                       mementos.put(id, memento);
                                   }
                                    
                                }
                            };
				
                               
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
		public void halted() {
			//no-op			
		}

		@Override
		public NucleotideFastaFileDataStore build() {
			return new IndexedNucleotideSequenceFastaFileDataStore(parser,filter,recordFilter, decodingOptions, mementos);
		}
	
	}
	
	

	private static class SingleRecordVisitor implements FastaVisitor{
		private NucleotideFastaRecord fastaRecord=null;
		private final DecodingOptions decodingOptions;
		
		private SingleRecordVisitor(DecodingOptions decodingOptions) {
			this.decodingOptions = decodingOptions;
		}

		@Override
		public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(fastaRecord !=null){
				callback.haltParsing();
				return null;
			}
			return new AbstractNucleotideFastaRecordVisitor(id, optionalComment, decodingOptions, false) {
				
				@Override
				protected void visitRecord(NucleotideFastaRecord fastaRecord) {
					SingleRecordVisitor.this.fastaRecord = fastaRecord;
					
				}
			};
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		@Override
		public void halted() {
			//no-op			
		}
	}

}
