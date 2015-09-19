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
package org.jcvi.jillion.internal.fasta.aa;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.fasta.aa.AbstractProteinFastaRecordVisitor;
import org.jcvi.jillion.fasta.aa.ProteinFastaDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

/**
 * {@code IndexedProteinFastaFileDataStore} is an implementation of 
 * {@link ProteinFastaDataStore} that only stores an index containing
 * file offsets to the various {@link FastaRecord}s contained
 * inside the fasta file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fasta record
 * must be seek'ed to and then re-parsed each time and the fasta file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedProteinFastaFileDataStore{

	private IndexedProteinFastaFileDataStore(){
		//can not instantiate
	}
	/**
	 * Creates a new {@link IndexedProteinFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedProteinFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedProteinFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static ProteinFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept(), null);
	}
	
	/**
	 * Creates a new {@link IndexedProteinFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedProteinFastaFileDataStore}
	 * for.
	 * @param filter the {@link DataStoreFilter} instance used to filter out the fasta records;
	 * can not be null.
	 * @return a new instance of {@link IndexedProteinFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static ProteinFastaDataStore create(File fastaFile, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter) throws IOException{
		FastaParser parser = FastaFileParser.create(fastaFile);
		return create(parser, filter, recordFilter);
	}
	
	/**
	 * Creates a new {@link IndexedProteinFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedProteinFastaFileDataStore}
	 * for.
	 * @param filter the {@link DataStoreFilter} instance used to filter out the fasta records;
	 * can not be null.
	 * @return a new instance of {@link IndexedProteinFastaFileDataStore};
	 * never null.
	 * @throws FileNotFoundException if the input fasta file does not exist.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static ProteinFastaDataStore create(FastaParser parser, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter) throws IOException{

		IndexedProteinFastaDataStoreBuilderVisitor builder = new IndexedProteinFastaDataStoreBuilderVisitor(parser, filter, recordFilter);
		builder.initialize();
		return builder.build();
	}
	
	
	
	
	
	private static final class IndexedProteinFastaDataStoreBuilderVisitor implements FastaVisitor, Builder<ProteinFastaDataStore> {
	
		private final Predicate<String> filter;
		private final  Predicate<ProteinFastaRecord> recordFilter;
		private final FastaParser parser;
		
		private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos = new LinkedHashMap<String, FastaVisitorCallback.FastaVisitorMemento>();
		
		private IndexedProteinFastaDataStoreBuilderVisitor(FastaParser parser, Predicate<String> filter,  Predicate<ProteinFastaRecord> recordFilter) throws IOException {
			this.filter = filter;
			this.parser = parser;
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
				FastaVisitorMemento memento = callback.createMemento();
				if(recordFilter ==null){
					mementos.put(id, memento);
				}else{
					return new AbstractProteinFastaRecordVisitor(id, optionalComment){

						@Override
						protected void visitRecord(
								ProteinFastaRecord fastaRecord) {
							if(recordFilter.test(fastaRecord)){
								mementos.put(id, memento);
							}
							
						}
						
					};
				}
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
		public ProteinFastaDataStore build() {
			return new Impl(parser, filter, recordFilter, mementos);
		}
	
	}
	
	public static final class Impl implements ProteinFastaDataStore {
		private volatile boolean closed =false;
		private final FastaParser parser;
		private final Predicate<String> filter;
		private final  Predicate<ProteinFastaRecord> recordFilter;
		
		private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos;
		
		
		public Impl(FastaParser parser, Predicate<String> filter, Predicate<ProteinFastaRecord> recordFilter,  Map<String, FastaVisitorMemento> mementos) {
			this.parser = parser;
			this.mementos = mementos;
			this.filter = filter;
			this.recordFilter = recordFilter;
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this,mementos.keySet().iterator());
		}

		@Override
		public ProteinFastaRecord get(String id)
				throws DataStoreException {
			throwExceptionIfClosed();
			if(!mementos.containsKey(id)){
				return null;
			}
			SingleRecordVisitor visitor = new SingleRecordVisitor();
			try {
				parser.parse(visitor, mementos.get(id));
				return visitor.fastaRecord;
			} catch (IOException e) {
				throw new DataStoreException("error reading fasta file",e);
			}
		}

		@Override
		public StreamingIterator<ProteinFastaRecord> iterator() throws DataStoreException {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this,
					LargeProteinFastaIterator.createNewIteratorFor(parser,filter, recordFilter));
		}
		
		
		@Override
		public StreamingIterator<DataStoreEntry<ProteinFastaRecord>> entryIterator()
				throws DataStoreException {
			throwExceptionIfClosed();
			StreamingIterator<DataStoreEntry<ProteinFastaRecord>> entryIter = new StreamingIterator<DataStoreEntry<ProteinFastaRecord>>(){
				StreamingIterator<ProteinFastaRecord> iter = LargeProteinFastaIterator.createNewIteratorFor(parser,filter, recordFilter);

				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}

				@Override
				public void close() {
					iter.close();
				}

				@Override
				public DataStoreEntry<ProteinFastaRecord> next() {
					ProteinFastaRecord next = iter.next();
					return new DataStoreEntry<ProteinFastaRecord>(next.getId(), next);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
		        
			};
			
			return DataStoreStreamingIterator.create(this, entryIter);
		
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

	}

	private static class SingleRecordVisitor implements FastaVisitor{
		private ProteinFastaRecord fastaRecord=null;
		@Override
		public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(fastaRecord !=null){
				callback.haltParsing();
				return null;
			}
			return new AbstractProteinFastaRecordVisitor(id, optionalComment) {
				
				@Override
				protected void visitRecord(ProteinFastaRecord fastaRecord) {
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

