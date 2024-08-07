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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.trace.sff.SffVisitorCallback.SffVisitorMemento;



/**
 * {@code IndexedSffFileDataStore} is an implementation 
 * of {@link SffFileDataStore} that only stores an index containing
 * byte offsets to the various {@link SffFlowgram}s contained
 * in a single sff file.  This allows for large files to provide
 * random access without taking up much memory. The down side is each flowgram
 * must be re-parsed each time and the sff file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 *
 */
final class CompletelyParsedIndexedSffFileDataStore {
	
	private CompletelyParsedIndexedSffFileDataStore(){
		//can not instantiate
	}
	/**
	 * Create a new {@link SffFileDataStore} instance which only indexes
	 * byte offsets for each read.
	 * @param sffFile the sff file to create a datastore for.
	 * @return a new {@link SffFileDataStore} instance; never null.
	 * @throws IOException if there is a problem reading the file.
	 * @throws IllegalArgumentException if the given sffFile
	 * has more than {@link Integer#MAX_VALUE} reads.
	 * @throws NullPointerException if sffFile is null.
	 * @throws IllegalArgumentException if sffFile does not exist.
	 */
	public static SffFileDataStore create(File sffFile) throws IOException{
		return create(sffFile, DataStoreFilters.alwaysAccept());
	}
	/**
	 * Create a new {@link SffFileDataStore} instance which only indexes
	 * byte offsets for each read that is accepted by the given {@link DataStoreFilter}.
	 * @param sffFile the sff file to create a datastore for.
	 * @return a new {@link SffFileDataStore} instance; never null.
	 * @throws IOException if there is a problem reading the file.
	 * @throws IllegalArgumentException if the given sffFile
	 * has more than {@link Integer#MAX_VALUE} reads.
	 * @throws NullPointerException if sffFile is null.
	 * @throws IllegalArgumentException if sffFile does not exist.
	 */
	public static SffFileDataStore create(File sffFile, DataStoreFilter filter) throws IOException{
		Visitor visitor = new Visitor(filter);
		SffParser parser = SffFileParser.create(sffFile);
		parser.parse(visitor);
		
		return visitor.build(parser, sffFile, filter);
	}
	
	
	
	private static final class Visitor implements SffVisitor{
		private Map<String, SffVisitorMemento> mementos;
		
		private final DataStoreFilter filter;
		private NucleotideSequence keySequence,flowSequence;
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffVisitorCallback callback,
				SffCommonHeader header) {
			mementos = new LinkedHashMap<String,SffVisitorMemento>((int)header.getNumberOfReads());
			keySequence = header.getKeySequence();
			flowSequence = header.getFlowSequence();
		}

		@Override
		public SffFileReadVisitor visitRead(SffVisitorCallback callback,
				final SffReadHeader readHeader) {
			if(filter.accept(readHeader.getId())){
				mementos.put(readHeader.getId(), callback.createMemento());
			}
			//always skip read data we'll read it later
			return null;
		}

		@Override
		public void end() {
			//no-op
			
		}
	
		SffFileDataStore build(SffParser parser,File sffFile, DataStoreFilter filter){
			return new DataStoreImpl(parser, sffFile, filter, keySequence, flowSequence, mementos);
		}
		
	}
	
	
	private static class DataStoreImpl implements SffFileDataStore{
		private final SffParser parser; //parser has the file ref
		private volatile boolean closed=false;
		private final NucleotideSequence keySequence,flowSequence;
		private final Map<String, SffVisitorMemento> mementos;

		private final File sffFile;
		private final DataStoreFilter filter;
		public DataStoreImpl(SffParser parser, File sffFile, DataStoreFilter filter,
				NucleotideSequence keySequence, NucleotideSequence flowSequence,
				Map<String, SffVisitorMemento> mementos) {
			this.parser = parser;
			this.mementos = mementos;
			this.sffFile = sffFile;
			this.filter = filter;
			
			if(keySequence ==null){
				throw new NullPointerException("key sequence can not be null");
			}
			if(flowSequence ==null){
				throw new NullPointerException("flow sequence can not be null");
			}
			this.keySequence = keySequence;
			this.flowSequence = flowSequence;
		}
		
		@Override
		public NucleotideSequence getKeySequence() {
			return keySequence;
		}

		@Override
		public NucleotideSequence getFlowSequence() {
			return flowSequence;
		}
		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			checkNotYetClosed();
			return IteratorUtil.createStreamingIterator(mementos.keySet().iterator());
		}

		@Override
		public SffFlowgram get(String id) throws DataStoreException {
			checkNotYetClosed();
			SffVisitorMemento momento = mementos.get(id);
			if(momento == null){
				return null;
			}
			SingleRecordVisitor visitor = new SingleRecordVisitor();
			try {
				parser.parse(visitor, momento);
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
		public StreamingIterator<SffFlowgram> iterator() throws DataStoreException {
			checkNotYetClosed();
			return DataStoreStreamingIterator.create(this,
					SffFileIterator.createNewIteratorFor(sffFile,filter));
		}
		
		

		@Override
		public StreamingIterator<DataStoreEntry<SffFlowgram>> entryIterator()
				throws DataStoreException {
			checkNotYetClosed();
			StreamingIterator<DataStoreEntry<SffFlowgram>> iter = new StreamingIterator<DataStoreEntry<SffFlowgram>>(){
	    		StreamingIterator<SffFlowgram> flowgramIter = SffFileIterator.createNewIteratorFor(sffFile,filter);

				@Override
				public boolean hasNext() {
					return flowgramIter.hasNext();
				}

				@Override
				public void close() {
					flowgramIter.close();
				}

				@Override
				public DataStoreEntry<SffFlowgram> next() {
					SffFlowgram flowgram = flowgramIter.next();
					return new DataStoreEntry<SffFlowgram>(flowgram.getId(), flowgram);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
	    		
	    	};
			return DataStoreStreamingIterator.create(this,iter);
		}

		@Override
		public void close() throws IOException {
			closed=true;
			
		}
		
		private void checkNotYetClosed(){
			if(closed){
				throw new DataStoreClosedException("datastore is closed");
			}
		}
		
		
		
	}
	
	private static class SingleRecordVisitor implements SffVisitor{
		private SffFlowgram flowgram;
		@Override
		public void visitHeader(SffVisitorCallback callback,
				SffCommonHeader header) {
			//no-op
			
		}

		@Override
		public SffFileReadVisitor visitRead(final SffVisitorCallback callback,
				final SffReadHeader readHeader) {
			//we should only see the read we care about
			return new SffFileReadVisitor(){

				@Override
				public void visitReadData(SffReadData readData) {
					flowgram =SffFlowgramImpl.create(readHeader, readData);
					
				}

				@Override
				public void visitEnd() {
					callback.haltParsing();
					
				}
				
			};
		}

		@Override
		public void end() {
			//no-op
			
		}

		public final SffFlowgram getFlowgram() {
			return flowgram;
		}
		
	}
}
