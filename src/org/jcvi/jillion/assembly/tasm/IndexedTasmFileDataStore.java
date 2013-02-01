package org.jcvi.jillion.assembly.tasm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.jcvi.jillion.assembly.tasm.DefaultTasmContig.Builder;
import org.jcvi.jillion.assembly.tasm.TasmFileVisitor2.TasmContigVisitorCallback.TasmContigVisitorMemento;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;

public class IndexedTasmFileDataStore implements TasmContigDataStore{

	private final DataStore<Long> fullLengthSequenceDataStore;
	private final TasmFileParser2 parser;
	
	private final LinkedHashMap<String, TasmContigVisitorMemento> mementos;
	
	private volatile boolean closed=false;
	
	
	public static TasmContigDataStore create(File tasmFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		IndexVisitor visitor = new IndexVisitor(filter);
		TasmFileParser2 parser = TasmFileParser2.create(tasmFile);
		parser.accept(visitor);
		return new IndexedTasmFileDataStore(parser, fullLengthSequenceDataStore, visitor.mementos);
	}
	
	private IndexedTasmFileDataStore(TasmFileParser2 parser,
			DataStore<Long> fullLengthSequenceDataStore,
			LinkedHashMap<String, TasmContigVisitorMemento> mementos) {
		this.parser = parser;
		this.fullLengthSequenceDataStore = fullLengthSequenceDataStore;
		this.mementos = mementos;
	}

	private void checkNotYetClosed() throws DataStoreException{
		if(closed){
			throw new DataStoreException("closed");
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



	private static final class IndexVisitor implements TasmFileVisitor2{
		private final DataStoreFilter filter;
		private final LinkedHashMap<String, TasmContigVisitorMemento> mementos = new LinkedHashMap<String, TasmContigVisitorMemento>();
		
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
		public void visitIncompleteEnd() {
			//no-op
			
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		
	}

	private class SingleContigVisitor implements TasmFileVisitor2{
		private TasmContig contig;
		@Override
		public TasmContigVisitor visitContig(
				final TasmContigVisitorCallback callback, String contigId) {
			//assume first visit is the one we want
			return new AbstractTasmContigVisitor(contigId, fullLengthSequenceDataStore) {
				
				@Override
				protected void visitRecord(Builder builder) {
					contig= builder.build();
					callback.stopParsing();
				}
			};
		}

		@Override
		public void visitIncompleteEnd() {
			//no-op
			
		}

		@Override
		public void visitEnd() {
			//no-op
			
		}
		
	}
}
