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
package org.jcvi.jillion.core.datastore;

import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestEmptyDataStoreFilter {
    DataStoreFilter sut = DataStoreFilters.alwaysAccept();
    @Test
    public void alwaysTrue(){
        assertTrue(sut.accept("something"));
        assertTrue(sut.accept("12345"));
        assertTrue(sut.accept("blah blah"));
    }
}
