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
/*
 * Created on Apr 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.File;

import org.jcvi.jillion.trace.sff.SffFileDataStore;
import org.jcvi.jillion.trace.sff.LargeSffFileDataStore;


public class TestLargeSffFileDataStore extends AbstractTestSffFileDataStore{

    @Override
    protected SffFileDataStore parseDataStore(File in) throws Exception {
        
        return LargeSffFileDataStore.create(in);
    }

}
