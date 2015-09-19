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

import java.io.File;

public class TestNoXMLManifestIndexedSffFileDataStore extends AbstractTestSffFileDataStore{

    @Override
	protected File sffFileToUse() {
		return SFF_FILE_NO_XML;
	}

	@Override
	protected SffFileDataStore parseDataStore(File f) throws Exception {
		return ManifestIndexed454SffFileDataStore.create(f);
	}


}
