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
package org.jcvi.jillion.core.residue.aa;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestDefaultProteinFastaFileDataStore extends AbstractTestProteinFastaFileDataStore{

	public TestDefaultProteinFastaFileDataStore() throws IOException {
		super(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
	}

}
