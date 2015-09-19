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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.tigr.tasm.TasmVisitor.TasmVisitorCallback.TasmVisitorMemento;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
/**
 * {@code IndexedTasmFileDataStore} is a {@link TasmContigDataStore}
 * implementation that only stores the 
 * {@link TasmVisitorMemento}s for the 
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
	private final TasmParser parser;
	
	private final Map<String, TasmVisitorMemento> mementos;
	
	private volatile boolean closed=false;
	
	
	public static TasmContigDataStore create(File tasmFile, DataStore<Long> fullLengthSequenceDataStore, DataStoreFilter filter) throws IOException{
		IndexVisitor visitor = new IndexVisitor(filter);
		TasmParser parser = TasmFileParser.create(tasmFile);
		parser.parse(visitor);
		return new IndexedTasmFileDataStore(parser, fullLengthSequenceDataStore, visitor.mementos);
	}
	
	private IndexedTasmFileDataStore(TasmParser parser,
			DataStore<Long> fullLengthSequenceDataStore,
			Map<String, TasmVisitorMemento> mementos) {
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
		TasmVisitorMemento memento = mementos.get(id);
		if(memento==null){
			return null;
		}
		SingleContigVisitor visitor = new SingleContigVisitor();
		try {
			parser.parse(visitor, memento);
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



	@Override
	public StreamingIterator<DataStoreEntry<TasmContig>> entryIterator()
			throws DataStoreException {
		 StreamingIterator<DataStoreEntry<TasmContig>> iter = new StreamingIterator<DataStoreEntry<TasmContig>>(){
    		 StreamingIterator<TasmContig> delegate = iterator();
			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}

			@Override
			public void close() {
				delegate.close();
			}

			@Override
			public DataStoreEntry<TasmContig> next() {
				TasmContig trace = delegate.next();
				return new DataStoreEntry<TasmContig>(trace.getId(), trace);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
    		 
    	 };
		return DataStoreStreamingIterator.create(this, iter);
	}



	private static final class IndexVisitor implements TasmVisitor{
		private final DataStoreFilter filter;
		private final Map<String, TasmVisitorMemento> mementos = new LinkedHashMap<String, TasmVisitorMemento>();
		
		public IndexVisitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public TasmContigVisitor visitContig(
				TasmVisitorCallback callback, String contigId) {
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

	private class SingleContigVisitor implements TasmVisitor{
		private TasmContig contig;
		@Override
		public TasmContigVisitor visitContig(
				final TasmVisitorCallback callback, String contigId) {
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
