package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.trace.sff.SffFileParserCallback.SffFileMemento;



/**
 * {@code IndexedSffFileDataStore} is an implementation 
 * of {@link FlowgramDataStore} that only stores an index containing
 * byte offsets to the various {@link Flowgram}s contained
 * in a single sff file.  This allows for large files to provide
 * random access without taking up much memory. The down side is each flowgram
 * must be re-parsed each time and the sff file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 *
 */
class CompletelyParsedIndexedSffFileDataStore {
	
	private CompletelyParsedIndexedSffFileDataStore(){
		//can not instantiate
	}
	/**
	 * Create a new {@link FlowgramDataStore} instance which only indexes
	 * byte offsets for each read.
	 * @param sffFile the sff file to create a datastore for.
	 * @return a new {@link FlowgramDataStore} instance; never null.
	 * @throws IOException if there is a problem reading the file.
	 * @throws IllegalArgumentException if the given sffFile
	 * has more than {@link Integer#MAX_VALUE} reads.
	 * @throws NullPointerException if sffFile is null.
	 * @throws IllegalArgumentException if sffFile does not exist.
	 * @see #canCreateIndexedDataStore(File)
	 */
	public static FlowgramDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new {@link FlowgramDataStore} instance which only indexes
	 * byte offsets for each read that is accepted by the given {@link DataStoreFilter}.
	 * @param sffFile the sff file to create a datastore for.
	 * @return a new {@link FlowgramDataStore} instance; never null.
	 * @throws IOException if there is a problem reading the file.
	 * @throws IllegalArgumentException if the given sffFile
	 * has more than {@link Integer#MAX_VALUE} reads.
	 * @throws NullPointerException if sffFile is null.
	 * @throws IllegalArgumentException if sffFile does not exist.
	 * @see #canCreateIndexedDataStore(File)
	 */
	public static FlowgramDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffFileParser parser = new SffFileParser(sffFile);
		parser.accept(visitor);
		
		return visitor.build(parser);
	}
	
	
	
	private static final class Visitor implements SffFileVisitor{
		private Map<String, SffFileMemento> mementos;
		
		private final DataStoreFilter filter;
		
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			mementos = new LinkedHashMap<String,SffFileMemento>((int)header.getNumberOfReads());
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			if(filter.accept(readHeader.getId())){
				mementos.put(readHeader.getId(), callback.createMemento());
			}
			//always skip read data we'll read it later
			return null;
		}

		@Override
		public void endSffFile() {
			//no-op
			
		}
	
		FlowgramDataStore build(SffFileParser parser){
			return new DataStoreImpl(parser, mementos);
		}
		
	}
	
	
	private static class DataStoreImpl implements FlowgramDataStore{
		private final SffFileParser parser; //parser has the file ref
		private volatile boolean closed=false;
		
		private final Map<String, SffFileMemento> mementos;

		public DataStoreImpl(SffFileParser parser,
				Map<String, SffFileMemento> mementos) {
			this.parser = parser;
			this.mementos = mementos;
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			checkNotYetClosed();
			return IteratorUtil.createStreamingIterator(mementos.keySet().iterator());
		}

		@Override
		public Flowgram get(String id) throws DataStoreException {
			checkNotYetClosed();
			SffFileMemento momento = mementos.get(id);
			if(momento == null){
				return null;
			}
			SingleRecordVisitor visitor = new SingleRecordVisitor();
			try {
				parser.accept(visitor, momento);
			} catch (IOException e) {
				throw new DataStoreException("error reparsing file", e);
			}
			return visitor.getFlowgram();
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
		public StreamingIterator<Flowgram> iterator() throws DataStoreException {
			checkNotYetClosed();
			// too lazy to write faster implementation for now
			return new DataStoreIterator<Flowgram>(this);
		}

		@Override
		public void close() throws IOException {
			closed=true;
			
		}
		
		private void checkNotYetClosed() throws DataStoreException{
			if(closed){
				throw new DataStoreException("datastore is closed");
			}
		}
		
		
		
	}
	
	private static class SingleRecordVisitor implements SffFileVisitor{
		private Flowgram flowgram;
		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			//no-op
			
		}

		@Override
		public SffFileReadVisitor visitRead(SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			//we should only see the read we care about
			return new SffFileReadVisitor(){

				@Override
				public void visitReadData(SffFileParserCallback callback,
						SffReadData readData) {
					flowgram =SffFlowgram.create(readHeader, readData);
					
				}

				@Override
				public void visitEndOfRead(SffFileParserCallback callback) {
					callback.stopParsing();
					
				}
				
			};
		}

		@Override
		public void endSffFile() {
			//no-op
			
		}

		public final Flowgram getFlowgram() {
			return flowgram;
		}
		
	}
}
