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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
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
				DataStore.of(map, SffFileDataStore.class));
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
		public StreamingIterator<DataStoreEntry<SffFlowgram>> entryIterator()
				throws DataStoreException {
			return delegate.entryIterator();
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
