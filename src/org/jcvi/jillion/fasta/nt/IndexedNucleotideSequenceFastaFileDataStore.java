/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
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
final class IndexedNucleotideSequenceFastaFileDataStore implements NucleotideFastaDataStore{
	
	private volatile boolean closed =false;
	private final FastaParser parser;
	private final DataStoreFilter filter;
	private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos;
	
	
	private IndexedNucleotideSequenceFastaFileDataStore(FastaParser parser, DataStoreFilter filter, Map<String, FastaVisitorMemento> mementos) {
		this.parser = parser;
		this.mementos = mementos;
		this.filter = filter;
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
		SingleRecordVisitor visitor = new SingleRecordVisitor();
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
					LargeNucleotideSequenceFastaIterator.createNewIteratorFor(parser,filter));
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
				StreamingIterator<NucleotideFastaRecord> iter = LargeNucleotideSequenceFastaIterator.createNewIteratorFor(parser,filter);

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
	 * Creates a new {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @return a new instance of {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * never null.
	 * @throws IOException 
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Creates a new {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param fastaFile the fasta to create an {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * for.
	 * @param filter the {@link DataStoreFilter} to use to filter the records from the fasta file.
	 * @return a new instance of {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * never null.
	 * @throws IOException  if there is a problem parsing the fasta file.
	 * @throws NullPointerException if the input fasta file is null.
	 */
	public static NucleotideFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		BuilderVisitor builder = createBuilder(fastaFile, filter);
		builder.initialize();
		return builder.build();
	}
	
	/**
	 * Creates a new {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param parser the {@link FastaParser} to use to create an {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * can not be null.
	 * @return a new instance of {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * never null.
	 * @throws IOException if there is a problem parsing the fasta data.
	 * @throws NullPointerException if the parser is null.
	 */
	public static NucleotideFastaDataStore create(FastaParser parser) throws IOException{
		return create(parser, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Creates a new {@link IndexedNucleotideSequenceFastaFileDataStore}
	 * instance using the given fastaFile.
	 * @param parser the {@link FastaParser} to use to create an {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * can not be null.
	 * @param filter the {@link DataStoreFilter} to use to filter the records from the fasta file.
	 * @return a new instance of {@link IndexedNucleotideSequenceFastaFileDataStore};
	 * never null.
	 * @throws IOException if there is a problem parsing the fasta data.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static NucleotideFastaDataStore create(FastaParser parser, DataStoreFilter filter) throws IOException{
		BuilderVisitor builder = createBuilder(parser, filter);
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
	private static BuilderVisitor createBuilder(File fastaFile, DataStoreFilter filter) throws IOException{
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		return new BuilderVisitor(fastaFile,filter);
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
	 * @throws IOException if the given fasta file does not exist.
	 * @throws NullPointerException if the input fasta file or filter are null.
	 */
	private static BuilderVisitor createBuilder(FastaParser parser, DataStoreFilter filter) throws IOException{
		if(filter ==null){
			throw new NullPointerException("filter can not be null");
		}
		if(parser ==null){
			throw new NullPointerException("parser can not be null");
		}
		return new BuilderVisitor(parser,filter);
	}
	
	
	

	
	private static final class BuilderVisitor implements FastaVisitor, Builder<NucleotideFastaDataStore> {
		
		private final DataStoreFilter filter;
		private final FastaParser parser;
		
		private final Map<String, FastaVisitorCallback.FastaVisitorMemento> mementos = new LinkedHashMap<String, FastaVisitorCallback.FastaVisitorMemento>();
		
		public BuilderVisitor(File fastaFile, DataStoreFilter filter) throws IOException {
			this(FastaFileParser.create(fastaFile), filter);

		}
		
		public BuilderVisitor(FastaParser parser, DataStoreFilter filter){
			this.filter = filter;
			this.parser =parser;
		}

		public void initialize() throws IOException {
			parser.parse(this);
			
		}

		@Override
		public FastaRecordVisitor visitDefline(FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(filter.accept(id)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("must be able to create memento");
				}
				mementos.put(id, callback.createMemento());
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
		public NucleotideFastaDataStore build() {
			return new IndexedNucleotideSequenceFastaFileDataStore(parser,filter,mementos);
		}
	
	}
	
	

	private static class SingleRecordVisitor implements FastaVisitor{
		private NucleotideFastaRecord fastaRecord=null;
		@Override
		public FastaRecordVisitor visitDefline(final FastaVisitorCallback callback,
				String id, String optionalComment) {
			if(fastaRecord !=null){
				callback.haltParsing();
				return null;
			}
			return new AbstractNucleotideFastaRecordVisitor(id, optionalComment) {
				
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
