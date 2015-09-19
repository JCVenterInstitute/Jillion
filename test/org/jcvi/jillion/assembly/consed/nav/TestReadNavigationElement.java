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
package org.jcvi.jillion.assembly.consed.nav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.jcvi.jillion.assembly.consed.nav.ConsensusNavigationElement;
import org.jcvi.jillion.assembly.consed.nav.ReadNavigationElement;
import org.jcvi.jillion.assembly.consed.nav.NavigationElement.Type;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestReadNavigationElement {
    String id= "contig id";
    Range range = Range.of(5,10);
    String comment = "this is a comment";
    ReadNavigationElement sut = new ReadNavigationElement(id, range,comment);
    
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNPE(){
        new ReadNavigationElement(null, range);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        new ReadNavigationElement(id, null);
    }
    
    @Test
    public void noComment(){
        ReadNavigationElement element = new ReadNavigationElement(id, range);
        assertEquals(id, element.getTargetId());
        assertEquals(range, element.getUngappedPositionRange());
        assertEquals(Type.READ, element.getType());
        assertNull(element.getComment());
    }
    
    @Test
    public void withComment(){
        assertEquals(id, sut.getTargetId());
        assertEquals(range, sut.getUngappedPositionRange());
        assertEquals(Type.READ, sut.getType());
        assertEquals(comment,sut.getComment());
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        ReadNavigationElement sameValues = new ReadNavigationElement(id, range,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentIdShouldNotBeEqual(){
        ReadNavigationElement differentId = new ReadNavigationElement("different"+id, range,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    @Test
    public void differentRangeShouldNotBeEqual(){
        ReadNavigationElement differentRange = new ReadNavigationElement(id, new Range.Builder(range).contractEnd(2).build(),comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
    @Test
    public void differentCommentShouldNotBeEqual(){
        ReadNavigationElement differentComment = new ReadNavigationElement(id, range,"different"+comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentComment);
    }
    @Test
    public void differentTypeShouldNotBeEqual(){
        ConsensusNavigationElement differentType = new ConsensusNavigationElement(id, range,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentType);
    }
    @Test
    public void noCommentShouldNotBeEqual(){
        ReadNavigationElement noComment = new ReadNavigationElement(id, range);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, noComment);
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentObj(){
        assertFalse(sut.equals("not a navigation element"));
    }
}
