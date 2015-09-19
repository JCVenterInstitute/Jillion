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
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public abstract class AbstractTestUnixAndDosFormatsParsedCorrectly {

	private final NucleotideFastaDataStore unixDataStore,dosDataStore;
	
	protected abstract NucleotideFastaDataStore createDataStoreFor(File fastaFile) throws IOException;
	
	public AbstractTestUnixAndDosFormatsParsedCorrectly() throws IOException{
		ResourceHelper resources = new ResourceHelper(AbstractTestUnixAndDosFormatsParsedCorrectly.class);
		
		File unixFile = resources.getFile("files/19150.fasta");
		File dosFile = resources.getFile("files/19150.dos.fasta");
		
		unixDataStore = createDataStoreFor(unixFile);
		dosDataStore = createDataStoreFor(dosFile);
	}
	@Test
	public void haveSameNumberOfRecords() throws IOException, DataStoreException{		
		assertEquals(unixDataStore.getNumberOfRecords(), dosDataStore.getNumberOfRecords());
	}
	
	@Test
	public void iteratorsMatch() throws DataStoreException{
		StreamingIterator<NucleotideFastaRecord> unixIter = unixDataStore.iterator();
		StreamingIterator<NucleotideFastaRecord> dosIter = dosDataStore.iterator();
		
		
		while(unixIter.hasNext()){
			assertTrue(dosIter.hasNext());
			assertEquals(unixIter.next(), dosIter.next());
		}
		assertFalse(dosIter.hasNext());
	}
	@Test
	public void IdIteratorsMatch() throws DataStoreException{
		StreamingIterator<String> unixIter = unixDataStore.idIterator();
		StreamingIterator<String> dosIter = dosDataStore.idIterator();		
		
		while(unixIter.hasNext()){
			assertTrue(dosIter.hasNext());
			assertEquals(unixIter.next(), dosIter.next());
		}
		assertFalse(dosIter.hasNext());
	}
	
	@Test
	public void get() throws DataStoreException{
		StreamingIterator<NucleotideFastaRecord> unixIter = unixDataStore.iterator();
		
		
		while(unixIter.hasNext()){
			NucleotideFastaRecord unix = unixIter.next();
			assertEquals(unix, dosDataStore.get(unix.getId()));
		}
	}
}
