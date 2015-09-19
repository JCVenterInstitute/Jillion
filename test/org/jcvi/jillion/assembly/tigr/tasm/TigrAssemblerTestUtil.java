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
