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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultExcludeDataStoreFilter {
    DataStoreFilter sut = DataStoreFilters.newExcludeFilter(Arrays.asList("include_1", "include_2"));
    
    @Test
    public void idIsInIncludeListShouldNotAccept(){
        assertFalse(sut.accept("include_1"));
        assertFalse(sut.accept("include_2"));
    }
    
    @Test
    public void idIsNotInIncludeListShouldAccept(){
        assertTrue(sut.accept("include_3"));
        assertTrue(sut.accept("something completely different"));
    }
}
