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

import org.jcvi.jillion.assembly.consed.nav.ConsensusNavigationElement;
import org.jcvi.jillion.assembly.consed.nav.ReadNavigationElement;
import org.jcvi.jillion.assembly.consed.nav.NavigationElement.Type;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsensusNavigationElement {

    String id= "contig id";
    Range range = Range.of(5,10);
    String comment = "this is a comment";
    ConsensusNavigationElement sut = new ConsensusNavigationElement(id, range,comment);
    
    @Test(expected = NullPointerException.class)
    public void nullIdShouldThrowNPE(){
        new ConsensusNavigationElement(null, range);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        new ConsensusNavigationElement(id, null);
    }
    
    @Test
    public void noComment(){
        ConsensusNavigationElement element = new ConsensusNavigationElement(id, range);
        assertEquals(id, element.getTargetId());
        assertEquals(range, element.getUngappedPositionRange());
        assertEquals(Type.CONSENSUS, element.getType());
        assertNull(element.getComment());
    }
    
    @Test
    public void withComment(){
        assertEquals(id, sut.getTargetId());
        assertEquals(range, sut.getUngappedPositionRange());
        assertEquals(Type.CONSENSUS, sut.getType());
        assertEquals(comment,sut.getComment());
    }
    
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        ConsensusNavigationElement sameValues = new ConsensusNavigationElement(id, range,comment);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentIdShouldNotBeEqual(){
        ConsensusNavigationElement differentId = new ConsensusNavigationElement("different"+id, range,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    @Test
    public void differentRangeShouldNotBeEqual(){
        ConsensusNavigationElement differentRange = new ConsensusNavigationElement(id, new Range.Builder(range).contractEnd(2).build(),comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
    @Test
    public void differentCommentShouldNotBeEqual(){
        ConsensusNavigationElement differentComment = new ConsensusNavigationElement(id, range,"different"+comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentComment);
    }
    @Test
    public void differentTypeShouldNotBeEqual(){
        ReadNavigationElement differentType = new ReadNavigationElement(id, range,comment);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentType);
    }
    @Test
    public void noCommentShouldNotBeEqual(){
        ConsensusNavigationElement noComment = new ConsensusNavigationElement(id, range);
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
