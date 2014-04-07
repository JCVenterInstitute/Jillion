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
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyTestUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public final class TigrAssemblerTestUtil {

	private TigrAssemblerTestUtil(){}
	
	public static void assertAllReadsCorrectlyPlaced(Contig<? extends AssembledRead> expected, TasmContig actual){
		StreamingIterator<? extends AssembledRead> iter=null;
		try{
			iter = expected.getReadIterator();
			while(iter.hasNext()){
				AssembledRead expectedRead = iter.next();
				AssemblyTestUtil.assertPlacedReadCorrect(expectedRead, actual.getRead(expectedRead.getId()));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
	
	public static enum FakeFullLengthDataStore implements DataStore<Long>{

		
    	INSTANCE;
    	/**
    	 * Always return the same long length should be long enough for sanger reads
    	 */
    	private static final Long LENGTH = Long.valueOf(1200L);
    	
		@Override
		public void close() throws IOException {
			//no-op
			
		}

		@Override
		public StreamingIterator<DataStoreEntry<Long>> entryIterator()
				throws DataStoreException {
			//isn't used so we can throw exception
			throw new UnsupportedOperationException();
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			//isn't used so we can throw exception
			throw new UnsupportedOperationException();
		}

		@Override
		public Long get(String id) throws DataStoreException {			
			return LENGTH;
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			//fake 
			return true;
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			//fake
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isClosed() {
			return false;
		}

		@Override
		public StreamingIterator<Long> iterator() throws DataStoreException {
			//isn't used so we can throw exception
			throw new UnsupportedOperationException();
		}
    	
    }
}
