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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

final class SffDataStoreBuilder{

	private final NucleotideSequence keySequence,flowSequence;
	
	private final Map<String, SffFlowgram> map;
	
	public SffDataStoreBuilder(NucleotideSequence keySequence, NucleotideSequence flowSequence){
		if(keySequence==null){
			throw new NullPointerException("key sequence can not be null");
		}
		if(flowSequence==null){
			throw new NullPointerException("flow sequence can not be null");
		}
		map = new LinkedHashMap<String, SffFlowgram>();
		this.keySequence = keySequence;
		this.flowSequence = flowSequence;
	}
	
	public SffDataStoreBuilder(NucleotideSequence keySequence, NucleotideSequence flowSequence, int initialCapacity){
		
		if(keySequence==null){
			throw new NullPointerException("key sequence can not be null");
		}
		if(flowSequence==null){
			throw new NullPointerException("flow sequence can not be null");
		}
		map = new LinkedHashMap<String, SffFlowgram>(initialCapacity);
		this.keySequence = keySequence;
		this.flowSequence = flowSequence;
	}

	public SffFileDataStore build() {
		return new DefaultSffFileDataStore(keySequence, flowSequence, 
				DataStoreUtil.adapt(SffFileDataStore.class,map));
	}

	public SffDataStoreBuilder addFlowgram(SffFlowgram flowgram) {
		map.put(flowgram.getId(), flowgram);
		return this;
	}
	
	private static final class DefaultSffFileDataStore implements SffFileDataStore{
		private final NucleotideSequence keySequence,flowSequence;
		private final DataStore<SffFlowgram> delegate;
		
		
		
		public DefaultSffFileDataStore(NucleotideSequence keySequence,
				NucleotideSequence flowSequence, DataStore<SffFlowgram> delegate) {
			this.keySequence = keySequence;
			this.flowSequence = flowSequence;
			this.delegate = delegate;
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public SffFlowgram get(String id) throws DataStoreException {
			return delegate.get(id);
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			return delegate.contains(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			return delegate.getNumberOfRecords();
		}

		@Override
		public boolean isClosed() {
			return delegate.isClosed();
		}

		@Override
		public StreamingIterator<SffFlowgram> iterator() throws DataStoreException {
			return delegate.iterator();
		}

		@Override
		public void close() throws IOException {
			delegate.close();			
		}
		@Override
		public final NucleotideSequence getKeySequence() {
			return keySequence;
		}
		@Override
		public final NucleotideSequence getFlowSequence() {
			return flowSequence;
		}
		
		
	}
}
