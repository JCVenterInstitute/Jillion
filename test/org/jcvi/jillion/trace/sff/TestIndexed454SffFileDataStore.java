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

import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
public class TestIndexed454SffFileDataStore extends AbstractTestSffFileDataStore{

   
    
    
    @Test
    public void noIndexInSffShouldReturnNull() throws IOException{
    	assertNull(ManifestIndexed454SffFileDataStore.create(SFF_FILE_NO_INDEX));
    }

	@Override
	protected SffFileDataStore parseDataStore(File f) throws Exception {
		return  ManifestIndexed454SffFileDataStore.create(f);
	}

}
