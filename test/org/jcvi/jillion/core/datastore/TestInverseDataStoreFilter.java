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

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestInverseDataStoreFilter {

    private DataStoreFilter mock;
    private final String id = "id";
    private DataStoreFilter sut;
    @Before
    public void setup(){
        mock = createMock(DataStoreFilter.class);  
        sut = DataStoreFilters.invertFilter(mock);
    }
    
    @Test
    public void wrappedFilterSaysTrueShouldReturnFalse(){
        expect(mock.accept(id)).andReturn(true);
        replay(mock);
        assertFalse(sut.accept(id));
        verify(mock);
    }
    @Test
    public void wrappedFilterSaysFalseShouldReturnTrue(){
        expect(mock.accept(id)).andReturn(false);
        replay(mock);
        assertTrue(sut.accept(id));
        verify(mock);
    }
}
