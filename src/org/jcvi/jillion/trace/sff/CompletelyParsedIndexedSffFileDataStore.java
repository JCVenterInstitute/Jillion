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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.trace.sff.SffFileParserCallback.SffFileMemento;



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
class CompletelyParsedIndexedSffFileDataStore {
	
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
		SffFileParser parser = SffFileParser.create(sffFile);
		parser.accept(visitor);
		
		return visitor.build(parser);
	}
	
	
	
	private static final class Visitor implements SffFileVisitor{
		private Map<String, SffFileMemento> mementos;
		
		private final DataStoreFilter filter;
		private NucleotideSequence keySequence,flowSequence;
		
		public Visitor(DataStoreFilter filter) {
			this.filter = filter;
		}

		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			mementos = new LinkedHashMap<String,SffFileMemento>((int)header.getNumberOfReads());
			keySequence = header.getKeySequence();
			flowSequence = header.getFlowSequence();
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
		public void end() {
			//no-op
			
		}
	
		SffFileDataStore build(SffFileParser parser){
			return new DataStoreImpl(parser, keySequence, flowSequence, mementos);
		}
		
	}
	
	
	private static class DataStoreImpl implements SffFileDataStore{
		private final SffFileParser parser; //parser has the file ref
		private volatile boolean closed=false;
		private final NucleotideSequence keySequence,flowSequence;
		private final Map<String, SffFileMemento> mementos;

		public DataStoreImpl(SffFileParser parser,
				NucleotideSequence keySequence, NucleotideSequence flowSequence,
				Map<String, SffFileMemento> mementos) {
			this.parser = parser;
			this.mementos = mementos;
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
		public StreamingIterator<SffFlowgram> iterator() throws DataStoreException {
			checkNotYetClosed();
			// too lazy to write faster implementation for now
			return new DataStoreIterator<SffFlowgram>(this);
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
	
	private static class SingleRecordVisitor implements SffFileVisitor{
		private SffFlowgram flowgram;
		@Override
		public void visitHeader(SffFileParserCallback callback,
				SffCommonHeader header) {
			//no-op
			
		}

		@Override
		public SffFileReadVisitor visitRead(final SffFileParserCallback callback,
				final SffReadHeader readHeader) {
			//we should only see the read we care about
			return new SffFileReadVisitor(){

				@Override
				public void visitReadData(SffReadData readData) {
					flowgram =SffFlowgramImpl.create(readHeader, readData);
					
				}

				@Override
				public void visitEnd() {
					callback.stopParsing();
					
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
