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

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
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
			TasmFileParser.create(contigFile).accept(visitor);
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
			TasmFileParser.create(contigFile).accept(visitor);
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
				TasmFileParser.create(contigFile).accept(visitor);
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
	public void close() throws IOException {
		closed=true;
		
	}
	
	
	private class IdIterator extends AbstractBlockingStreamingIterator<String>{
		
		@Override
		protected void backgroundThreadRunMethod() throws RuntimeException {
			TasmFileVisitor visitor = new TasmFileVisitor() {
				
				@Override
				public void halted() {
					//no-op				
				}
				
				@Override
				public void visitEnd() {
					//no-op				
				}
				
				@Override
				public TasmContigVisitor visitContig(TasmContigVisitorCallback callback,
						String contigId) {
					if(filter.accept(contigId)){
						blockingPut(contigId);
					}
					return null;
				}
			};
			
			try {
				TasmFileParser.create(contigFile).accept(visitor);
			} catch (IOException e) {
				throw new RuntimeException(ERROR_PARSING_CONTIG_FILE,e);
			}
		}
	}
	
	private static final class ContainsVisitor implements TasmFileVisitor{
		private final String id;
		private boolean contains=false;
		
		public ContainsVisitor(String id) {
			this.id = id;
		}

		@Override
		public TasmContigVisitor visitContig(
				TasmContigVisitorCallback callback, String contigId) {
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
	
	private class GetVisitor implements TasmFileVisitor{
		private final String id;
		
		private TasmContig contig;
		
		public GetVisitor(String id) {
			this.id = id;
		}

		@Override
		public TasmContigVisitor visitContig(
				final TasmContigVisitorCallback callback, String contigId) {
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
	
	
	
	private class SizeVisitor implements TasmFileVisitor{
		private long size = 0L;

		@Override
		public TasmContigVisitor visitContig(
				TasmContigVisitorCallback callback, String contigId) {
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
