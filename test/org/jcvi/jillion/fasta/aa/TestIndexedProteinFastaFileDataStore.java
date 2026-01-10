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
package org.jcvi.jillion.fasta.aa;

import java.io.File;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.fasta.aa.ProteinFastaDataStore;
import org.jcvi.jillion.internal.fasta.aa.IndexedProteinFastaFileDataStore;
import org.jcvi.jillion.spi.InvalidCharacterHandler;

public class TestIndexedProteinFastaFileDataStore extends AbstractTestProteinFastaDataStore{


	public TestIndexedProteinFastaFileDataStore() throws Exception {
		super();
	}

	@Override
	protected ProteinFastaDataStore create(File fastaFile) throws Exception{
		return IndexedProteinFastaFileDataStore.create(fastaFile);
	}

	@Override
	protected ProteinFastaDataStore create(File fastaFile, InvalidCharacterHandler invalidCharacterHandler) throws Exception {
		return new ProteinFastaFileDataStoreBuilder(fastaFile)
				.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
				.invalidCharacterHandler(invalidCharacterHandler)
				.build();
	}
}
