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
package org.jcvi.jillion.experimental.plate;

import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestWellFactories {

    Well sut = Well.create("A01");
    
    @Test(expected = NullPointerException.class)
    public void nullNameShouldThrowNPE(){
        Well.create(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void outOfIndexColumnShouldThrowIllegalArgumentException(){
        Well.create("A25");
    }
    @Test(expected = IllegalArgumentException.class)
    public void outOfIndexRowShouldThrowIllegalArgumentException(){
        Well.create("Z01");
    }
    
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void sameReferenceShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValueShouldBeEqual(){
        Well same = Well.create("A01");
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
}
