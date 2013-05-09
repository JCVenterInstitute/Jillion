/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.contig;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor.TigrContigVisitorCallback.TigrContigVisitorMemento;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code IndexedTigrContigFileDataStore} is an implementation of 
 * {@link TigrContigDataStore} that only stores an index containing
 * {@link TigrContigVisitorMemento}s to the various contigs contained
 * inside a contig file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each contig
 * must be re-parsed each time.
 * @author dkatzel
 *
 *
 */
final class IndexedTigrContigFileDataStore implements TigrContigDataStore {

	

	private final Map<String, TigrContigVisitorMemento> mementos;
	private final DataStore<Long> fullLengthSequences;
	private final DataStoreFilter filter;
	private final File contigFile;
	private volatile boolean closed=false;
	private final TigrContigFileParser parser;
	
	public static TigrContigDataStore create(File contigFile, DataStore<Long> fullLengthSequences, DataStoreFilter filter) throws IOException{
		TigrContigFileParser parser =TigrContigFileParser.create(contigFile);
		IndexedDataStorBuilder visitor = new IndexedDataStorBuilder(filter);
		parser.accept(visitor);
		return visitor.build(contigFile, fullLengthSequences, parser);
	}
	
	
	private IndexedTigrContigFileDataStore(File contigFile,
			DataStoreFilter filter,
			DataStore<Long> fullLengthSequences,
			TigrContigFileParser parser,
			Map<String, TigrContigVisitorMemento> mementos) {
		this.contigFile = contigFile;
		this.filter = filter;
		this.fullLengthSequences=fullLengthSequences;
		this.mementos = mementos;
		this.parser= parser;
	}

	private void checkNotClosed(){
		if(closed){
			throw new DataStoreClosedException("datastore is closed");
		}
	}

	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		checkNotClosed();		
		return DataStoreStreamingIterator.create(this, mementos.keySet().iterator());
	}


	@Override
	public TigrContig get(String id) throws DataStoreException {
		checkNotClosed();
		TigrContigVisitorMemento memento = mementos.get(id);
		if(memento ==null){
			//not in datastore
			return null;
		}
		SingleContigVisitor visitor = new SingleContigVisitor();
		try {
			parser.accept(visitor, memento);
		} catch (IOException e) {
			throw new DataStoreException("error parsing contig file to get " + id, e);
		}
		return visitor.contig;
	}


	@Override
	public boolean contains(String id) throws DataStoreException {
		checkNotClosed();
		return mementos.containsKey(id);
	}


	@Override
	public long getNumberOfRecords() throws DataStoreException {
		checkNotClosed();
		return mementos.size();
	}


	@Override
	public boolean isClosed() {
		return closed;
	}


	@Override
	public StreamingIterator<TigrContig> iterator() throws DataStoreException {
		checkNotClosed();
		return DataStoreStreamingIterator.create(this, 
				TigrContigFileContigIterator.create(contigFile, fullLengthSequences, filter));
	}


	@Override
	public void close() throws IOException {
		closed=true;
		mementos.clear();
		
	}
	
	private final class SingleContigVisitor implements TigrContigFileVisitor{
		TigrContig contig = null;
		@Override
		public TigrContigVisitor visitContig(
				final TigrContigVisitorCallback callback, String contigId) {
			//assume the first contig we see is the one we want
			return new AbstractTigrContigBuilderVisitor(contigId, fullLengthSequences) {
				
				@Override
				protected void visitContig(TigrContigBuilder builder) {
					SingleContigVisitor.this.contig = builder.build();
					callback.haltParsing();
				}
			};
		}

		@Override
		public void halted() {
			//no-op			
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		
	}
	
	
	
	
	
	private static final class IndexedDataStorBuilder implements TigrContigFileVisitor{
		private final DataStoreFilter filter;
		private final Map<String, TigrContigVisitorMemento> mementos = new LinkedHashMap<String, TigrContigVisitorMemento>();
		
		
		private IndexedDataStorBuilder(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public TigrContigVisitor visitContig(
				TigrContigVisitorCallback callback, String contigId) {
			if(filter.accept(contigId)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("indexed datastore needs to create mementos");
				}
				mementos.put(contigId, callback.createMemento());
			}
			//always skip actual contig data
			return null;
		}

		@Override
		public void halted() {
			//no-op			
		}

		@Override
		public void visitEnd() {
			//no-op			
		}
		
		public TigrContigDataStore build(File contigFile,
				DataStore<Long> fullLengthSequences,
				TigrContigFileParser parser){
			return new IndexedTigrContigFileDataStore(contigFile,
					filter,
					fullLengthSequences,
					parser, mementos);
		}
	}
}
