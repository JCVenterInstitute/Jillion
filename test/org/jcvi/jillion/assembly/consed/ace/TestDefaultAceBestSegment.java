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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.consed.ace.DefaultAceBaseSegment;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultAceBestSegment {

    String name = "name";
    Range range = Range.of(1,10);
    DefaultAceBaseSegment sut = new DefaultAceBaseSegment(name, range);
    
    @Test(expected = NullPointerException.class)
    public void nullNameShouldThrowNPE(){
        new DefaultAceBaseSegment(null, range);
    }
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        new DefaultAceBaseSegment(name,null);
    }
    
    @Test
    public void constructor(){
        assertEquals(name, sut.getReadName());
        assertEquals(range, sut.getGappedConsensusRange());
    }
    @Test
    public void differentClassNotEqual(){
        assertFalse(sut.equals("not a best segment"));
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesIsEqual(){
        DefaultAceBaseSegment sameValues = new DefaultAceBaseSegment(name, range);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentNameIsNotEqual(){
        DefaultAceBaseSegment differentName = new DefaultAceBaseSegment("different"+name, range);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentName);
    }
    
    @Test
    public void differentRangeIsNotEqual(){
        DefaultAceBaseSegment differentRange = new DefaultAceBaseSegment(name, new Range.Builder(range).shift(1).build());
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
}
