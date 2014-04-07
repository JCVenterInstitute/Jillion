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
package org.jcvi.jillion.assembly.tigr.contig;

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

final class LargeTigrContigFileDataStore implements TigrContigDataStore{

	private static final String ERROR_PARSING_CONTIG_FILE = "error parsing contig file";
	private final DataStoreFilter filter;
	private final File contigFile;
	private final DataStore<Long> fullLengthSequences;
	/**
	 * Lazy loaded.
	 */
	private Long size=null;
	
	private volatile boolean closed=false;
	
	public LargeTigrContigFileDataStore(File contigFile,
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
	public TigrContig get(String id) throws DataStoreException {
		//if it doesn't pass the filter 
		//we won't find it even if it's in the file
		if(!filter.accept(id)){
			return null;
		}
		GetVisitor visitor = new GetVisitor(id);
		try {
			TigrContigFileParser.create(contigFile).parse(visitor);
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
			TigrContigFileParser.create(contigFile).parse(visitor);
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
				TigrContigFileParser.create(contigFile).parse(visitor);
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
	public StreamingIterator<TigrContig> iterator() throws DataStoreException {
		checkNotClosed();
		return DataStoreStreamingIterator.create(this, 
					TigrContigFileContigIterator.create(contigFile, fullLengthSequences, filter));
	}

	
	@Override
	public StreamingIterator<DataStoreEntry<TigrContig>> entryIterator()
			throws DataStoreException {
		 StreamingIterator<DataStoreEntry<TigrContig>> iter = new StreamingIterator<DataStoreEntry<TigrContig>>(){
    		 StreamingIterator<TigrContig> delegate = TigrContigFileContigIterator.create(contigFile, fullLengthSequences, filter);
			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}

			@Override
			public void close() {
				delegate.close();
			}

			@Override
			public DataStoreEntry<TigrContig> next() {
				TigrContig trace = delegate.next();
				return new DataStoreEntry<TigrContig>(trace.getId(), trace);
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
			TigrContigFileVisitor visitor = new TigrContigFileVisitor() {
				
				@Override
				public void halted() {
					//no-op				
				}
				
				@Override
				public void visitEnd() {
					//no-op				
				}
				
				@Override
				public TigrContigVisitor visitContig(TigrContigVisitorCallback callback,
						String contigId) {
					if(filter.accept(contigId)){
						blockingPut(contigId);
					}
					return null;
				}
			};
			
			try {
				TigrContigFileParser.create(contigFile).parse(visitor);
			} catch (IOException e) {
				throw new RuntimeException(ERROR_PARSING_CONTIG_FILE,e);
			}
		}
	}
	
	private static final class ContainsVisitor implements TigrContigFileVisitor{
		private final String id;
		private boolean contains=false;
		
		public ContainsVisitor(String id) {
			this.id = id;
		}

		@Override
		public TigrContigVisitor visitContig(
				TigrContigVisitorCallback callback, String contigId) {
			if(id.equals(contigId)){
				contains=true;
				callback.haltParsing();
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
	
	private class GetVisitor implements TigrContigFileVisitor{
		private final String id;
		
		private TigrContig contig;
		
		public GetVisitor(String id) {
			this.id = id;
		}

		@Override
		public TigrContigVisitor visitContig(
				final TigrContigVisitorCallback callback, String contigId) {
			if(id.equals(contigId)){
				return new AbstractTigrContigBuilderVisitor(contigId, fullLengthSequences) {
					
					@Override
					protected void visitContig(TigrContigBuilder builder) {
						GetVisitor.this.contig = builder.build();
						callback.haltParsing();
					}
				};
			}
			return null;
		}

		

		public TigrContig getContig() {
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
	
	
	
	private class SizeVisitor implements TigrContigFileVisitor{
		private long size = 0L;

		@Override
		public TigrContigVisitor visitContig(
				TigrContigVisitorCallback callback, String contigId) {
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
