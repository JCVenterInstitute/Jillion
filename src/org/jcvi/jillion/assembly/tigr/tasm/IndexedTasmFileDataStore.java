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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.tigr.tasm.TasmFileVisitor.TasmContigVisitorCallback.TasmContigVisitorMemento;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code IndexedTasmFileDataStore} is a {@link TasmContigDataStore}
 * implementation that only stores the 
 * {@link TasmContigVisitorMemento}s for the 
 * contig ids in a tasm file 
 * that match the given {@link DataStoreFilter}.
 * This will keep memory usage small put require
 * additional I/O opperations to seek
 * to the specified location and reparse the contig during
 * calls to {@link #get(String)}.
 * @author dkatzel
 *
 */
final class IndexedTasmFileDataStore implements TasmContigDataStore{

	private final DataStore<Long> fullLengthSequenceDataStore;
	private final TasmFileParser parser;
	
	private final Map<String, TasmContigVisitorMemento> mementos;
	
	private volatile boolean closed=false;
	
	
	public static TasmContigDataStore create(File tasmFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		IndexVisitor visitor = new IndexVisitor(filter);
		TasmFileParser parser = TasmFileParser.create(tasmFile);
		parser.accept(visitor);
		return new IndexedTasmFileDataStore(parser, fullLengthSequenceDataStore, visitor.mementos);
	}
	
	private IndexedTasmFileDataStore(TasmFileParser parser,
			DataStore<Long> fullLengthSequenceDataStore,
			Map<String, TasmContigVisitorMemento> mementos) {
		this.parser = parser;
		this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
		this.mementos = mementos;
	}

	private void checkNotYetClosed(){
		if(closed){
			throw new DataStoreClosedException("closed");
		}
	}

	@Override
	public void close() throws IOException {
		closed=true;
		mementos.clear();
	}



	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		return DataStoreStreamingIterator.create(this, mementos.keySet().iterator());
	}



	@Override
	public TasmContig get(String id) throws DataStoreException {
		checkNotYetClosed();
		TasmContigVisitorMemento memento = mementos.get(id);
		if(memento==null){
			return null;
		}
		SingleContigVisitor visitor = new SingleContigVisitor();
		try {
			parser.accept(visitor, memento);
			return visitor.contig;
		} catch (IOException e) {
			throw new DataStoreException("error parsing contig " + id, e);
		}
	}



	@Override
	public boolean contains(String id) throws DataStoreException {
		checkNotYetClosed();		
		return mementos.containsKey(id);
	}



	@Override
	public long getNumberOfRecords() throws DataStoreException {
		checkNotYetClosed();	
		return mementos.size();
	}



	@Override
	public boolean isClosed() {
		return closed;
	}



	@Override
	public StreamingIterator<TasmContig> iterator() throws DataStoreException {
		return new DataStoreIterator<TasmContig>(this);
	}



	private static final class IndexVisitor implements TasmFileVisitor{
		private final DataStoreFilter filter;
		private final Map<String, TasmContigVisitorMemento> mementos = new LinkedHashMap<String, TasmContigVisitorMemento>();
		
		public IndexVisitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public TasmContigVisitor visitContig(
				TasmContigVisitorCallback callback, String contigId) {
			if(filter.accept(contigId)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("tasm parser must be able to create mementos");
				}
				mementos.put(contigId, callback.createMemento());
			}
			//always skip
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
		
	}

	private class SingleContigVisitor implements TasmFileVisitor{
		private TasmContig contig;
		@Override
		public TasmContigVisitor visitContig(
				final TasmContigVisitorCallback callback, String contigId) {
			//assume first visit is the one we want
			return new AbstractTasmContigBuilderVisitor(contigId, fullLengthSequenceDataStore) {
				
				@Override
				protected void visitRecord(TasmContigBuilder builder) {
					contig= builder.build();
					callback.halt();
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
}
