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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.junit.Test;
public class TestIndexedSffFileDataStore extends AbstractTestExampleSffFile{

	@Test
	public void returnManifestIndexedInstanceIfManifestIsPresent() throws IOException{
		SffFileDataStore datastore = new SffFileDataStoreBuilder(SFF_FILE)
										.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
										.build();
		assertTrue(datastore instanceof ManifestIndexed454SffFileDataStore);
	}
	@Test
	public void returnManifestIndexedInstanceIfManifestWithNoXMLIsPresent() throws IOException{
		SffFileDataStore datastore = new SffFileDataStoreBuilder(SFF_FILE_NO_XML)
												.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
												.build();
		assertTrue(datastore instanceof ManifestIndexed454SffFileDataStore);
	}
	
	@Test
	public void returnFullyParsedIndexedInstanceIfNoIndexIsPresent() throws IOException{
		SffFileDataStore datastore = new SffFileDataStoreBuilder(SFF_FILE_NO_INDEX)
												.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
												.build();
		assertNotNull(datastore);
		assertFalse(datastore instanceof ManifestIndexed454SffFileDataStore);
	}
}
