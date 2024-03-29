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

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
/**
 * {@code LargeTasmContigFileDataStore}
 * is a {@link TasmContigDataStore}
 * implementation that does not store
 * any records in memory.  This keeps
 * memory usage very low at the cost
 * of having the reparse the file each 
 * time a contig is requested.
 * @author dkatzel
 *
 */
final class LargeTasmContigFileDataStore implements TasmContigDataStore{

	private static final String ERROR_PARSING_CONTIG_FILE = "error parsing contig file";
	private final DataStoreFilter filter;
	private final File contigFile;
	private final DataStore<Long> fullLengthSequences;
	/**
	 * Lazy loaded.
	 */
	private Long size=null;
	
	private volatile boolean closed=false;
	
	public LargeTasmContigFileDataStore(File contigFile,
			DataStore<Long> fullLengthSequences, DataStoreFilter filter) {
		super();
		this.contigFile = contigFile;
		this.fullLengthSequences = fullLengthSequences;
		this.filter = filter;
	}

	private void checkNotClosed(){
		if(closed){
			throw new DataStoreClosedException("datastore is closed");
		}
	}
	@Override
	public StreamingIterator<String> idIterator() throws DataStoreException {
		checkNotClosed();
		IdIterator iter = new IdIterator();
		iter.start();
		return DataStoreStreamingIterator.create(this, iter);
	}

	@Override
	public TasmContig get(String id) throws DataStoreException {
		//if it doesn't pass the filter 
		//we won't find it even if it's in the file
		if(!filter.accept(id)){
			return null;
		}
		GetVisitor visitor = new GetVisitor(id);
		try {
			TasmFileParser.create(contigFile).parse(visitor);
			return visitor.getContig();
		} catch (IOException e) {
			throw new DataStoreException(ERROR_PARSING_CONTIG_FILE, e);
		}
	}

	@Override
	public boolean contains(String id) throws DataStoreException {
		//if it doesn't pass the filter
		//we won't find it
		if(!filter.accept(id)){
			return false;
		}
		ContainsVisitor visitor = new ContainsVisitor(id);
		try {
			TasmFileParser.create(contigFile).parse(visitor);
			return visitor.contains();
		} catch (IOException e) {
			throw new DataStoreException(ERROR_PARSING_CONTIG_FILE, e);
		}
	}

	@Override
	public synchronized long getNumberOfRecords() throws DataStoreException {
		if(size ==null){
			SizeVisitor visitor = new SizeVisitor();
			try {
				TasmFileParser.create(contigFile).parse(visitor);
				size = visitor.getSize();
			} catch (IOException e) {
				throw new DataStoreException(ERROR_PARSING_CONTIG_FILE, e);
			}
		}
		return size.longValue();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public StreamingIterator<TasmContig> iterator() throws DataStoreException {
		checkNotClosed();
		return DataStoreStreamingIterator.create(this, 
					TasmContigFileContigIterator.create(contigFile, fullLengthSequences, filter));
	}
	
	

	@Override
	public StreamingIterator<DataStoreEntry<TasmContig>> entryIterator()
			throws DataStoreException {
		 StreamingIterator<DataStoreEntry<TasmContig>> iter = new StreamingIterator<DataStoreEntry<TasmContig>>(){
    		 
			StreamingIterator<TasmContig> delegate = TasmContigFileContigIterator.create(contigFile, fullLengthSequences, filter);
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
				TasmContig asm = delegate.next();
				return new DataStoreEntry<TasmContig>(asm.getId(), asm);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
    		 
    	 };
		return DataStoreStreamingIterator.create(this, iter);
	}

	@Override
	public void close() throws IOException {
		closed=true;
		
	}
	
	
	private class IdIterator extends AbstractBlockingStreamingIterator<String>{
		
		@Override
		protected void backgroundThreadRunMethod() throws RuntimeException {
			TasmVisitor visitor = new TasmVisitor() {
				
				@Override
				public void halted() {
					//no-op				
				}
				
				@Override
				public void visitEnd() {
					//no-op				
				}
				
				@Override
				public TasmContigVisitor visitContig(TasmVisitorCallback callback,
						String contigId) {
					if(filter.accept(contigId)){
						blockingPut(contigId);
					}
					return null;
				}
			};
			
			try {
				TasmFileParser.create(contigFile).parse(visitor);
			} catch (IOException e) {
				throw new RuntimeException(ERROR_PARSING_CONTIG_FILE,e);
			}
		}
	}
	
	private static final class ContainsVisitor implements TasmVisitor{
		private final String id;
		private boolean contains=false;
		
		public ContainsVisitor(String id) {
			this.id = id;
		}

		@Override
		public TasmContigVisitor visitContig(
				TasmVisitorCallback callback, String contigId) {
			if(id.equals(contigId)){
				contains=true;
				callback.halt();
			}
			return null;
		}

		public boolean contains() {
			return contains;
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
	
	private class GetVisitor implements TasmVisitor{
		private final String id;
		
		private TasmContig contig;
		
		public GetVisitor(String id) {
			this.id = id;
		}

		@Override
		public TasmContigVisitor visitContig(
				final TasmVisitorCallback callback, String contigId) {
			if(id.equals(contigId)){
				return new AbstractTasmContigBuilderVisitor(contigId, fullLengthSequences) {
					
					@Override
					protected void visitRecord(TasmContigBuilder builder) {
						GetVisitor.this.contig = builder.build();
						callback.halt();
					}
				};
			}
			return null;
		}

		

		public TasmContig getContig() {
			return contig;
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
	
	
	
	private class SizeVisitor implements TasmVisitor{
		private long size = 0L;

		@Override
		public TasmContigVisitor visitContig(
				TasmVisitorCallback callback, String contigId) {
			if(filter.accept(contigId)){
				size++;
			}
			return null;
		}

		

		public long getSize() {
			return size;
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
