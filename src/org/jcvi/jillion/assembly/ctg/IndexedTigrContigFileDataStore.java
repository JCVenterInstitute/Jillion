package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.ctg.TigrContigFileVisitor.TigrContigVisitorCallback.TigrContigVisitorMemento;
import org.jcvi.jillion.core.datastore.DataStore;
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

	private void checkNotClosed() throws DataStoreException{
		if(closed){
			throw new DataStoreException("datastore is closed");
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
			return new AbstractTigrContigVisitor(contigId, fullLengthSequences) {
				
				@Override
				protected void visitContig(TigrContig contig) {
					SingleContigVisitor.this.contig = contig;
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
		public void visitIncompleteEnd() {
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
