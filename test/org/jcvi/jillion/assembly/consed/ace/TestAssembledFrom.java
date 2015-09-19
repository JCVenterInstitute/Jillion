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
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.consed.ace.AlignedReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssembledFrom {

    Direction dir = Direction.FORWARD;
    int offset = 12345;
    
    AlignedReadInfo sut = new AlignedReadInfo(offset, dir);
    
    @Test
    public void constructor(){
        assertEquals(offset, sut.getStartOffset());
        assertEquals(dir, sut.getDirection());
    }
    @Test(expected = NullPointerException.class)
    public void nullDirectionShouldThrowNPE(){
    	new AlignedReadInfo(offset,null);
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not an AssembledFrom"));
    }
    
    @Test
    public void equalsSameValues(){
        AlignedReadInfo sameValues = new AlignedReadInfo(offset, dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
  

    @Test
    public void differentOffsetShouldNotBeEqual(){
        AlignedReadInfo differentOffset = new AlignedReadInfo(offset+1, dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentOffset);
    }

    @Test
    public void differentComlimentShouldNotBeEqual(){
        AlignedReadInfo differentCompliment = new AlignedReadInfo(offset, Direction.REVERSE);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentCompliment);
    }
    
    @Test
    public void testToString(){
        String expected = "AlignedReadInfo [dir=" + dir + ", startOffset=" + offset
				+ "]";
        assertEquals(expected, sut.toString());
    }
}
